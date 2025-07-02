package pro.trevor.joe.program.llvm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import pro.trevor.joe.parser.tree.declaration.Access;
import pro.trevor.joe.program.File;
import pro.trevor.joe.program.Parameter;
import pro.trevor.joe.program.TopLevelType;
import pro.trevor.joe.program.analyzer.TypeAnalyzer;
import pro.trevor.joe.program.code.Function;
import pro.trevor.joe.program.extern.ExternFunction;
import pro.trevor.joe.program.extern.ExternVariant;
import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.program_enum.Enum;
import pro.trevor.joe.program.program_interface.Interface;
import pro.trevor.joe.program.type.*;

import java.util.List;

import static org.bytedeco.llvm.global.LLVM.*;

public class Generator implements AutoCloseable {

    private final File file;
    private final java.io.File output;

    final LlvmInstance llvm;
    final TypeAnalyzer typeAnalyzer;
    Function currentFunction;

    final StatementGenerator statementGenerator;

    public Generator(File file, java.io.File output, String triple) {
        this.file = file;
        this.output = output;
        this.llvm = new LlvmInstance(file.getName().split("\\.")[0], triple);
        this.typeAnalyzer = new TypeAnalyzer(this.file);
        this.currentFunction = null;
        this.statementGenerator = new StatementGenerator(this);
    }

    public Generator(File file, java.io.File output) {
        this.file = file;
        this.output = output;
        this.llvm = new LlvmInstance(file.getName().split("\\.")[0]);
        this.typeAnalyzer = new TypeAnalyzer(this.file);
        this.currentFunction = null;
        this.statementGenerator = new StatementGenerator(this);
    }


    public boolean generate() {
        addTopLevelTypes();
        addUndeclaredExternFunctions();
        addExternalFunctions();
        file.getFunctions().forEach(this::addDeclaredFunction);
        if (!write())  {
            return false;
        }
        return validate();
    }

    private void addUndeclaredExternFunctions() {
        addExternalFunction(new ExternFunction(Access.PRIVATE, ExternVariant.C, Intrinsics.MALLOC_NAME, List.of(Intrinsics.MALLOC_ARG), Intrinsics.MALLOC_RET, false));
    }

    private void addExternalFunctions() {
        file.getExternFunctions().forEach(this::addExternalFunction);
    }

    private void addDeclaredFunction(Function function) {
        currentFunction = function;
        LLVMValueRef llvmFunction = LLVMGetNamedFunction(llvm.module, function.getIdentifier());
        if (llvmFunction == null) {
            LLVMTypeRef returnType = getLLVMType(function.getReturnType());
            LLVMTypeRef[] paramTypes = function.getParameters().stream()
                    .map(Parameter::type).map(this::getLLVMType)
                    .toArray(LLVMTypeRef[]::new);
            PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramTypes);
            llvmFunction = LLVMAddFunction(llvm.module, function.getIdentifier(), LLVMFunctionType(returnType, params, paramTypes.length, 0));
        }

        typeAnalyzer.getContext().resetLocals();
        statementGenerator.addFunction(function, llvmFunction);
        currentFunction = null;
    }

    private void addEnum(Enum enumType) {

    }

    private void addInterface(Interface interfaceType) {

    }

    private void addTopLevelType(TopLevelType type) {
        switch (type) {
            case Class _class -> Util.declareClass(this, _class);
            case Enum _enum -> addEnum(_enum);
            case Interface _interface -> addInterface(_interface);
            default -> throw new IllegalStateException("Unknown top-level type " + type.getClass().getSimpleName());
        }
    }

    private void addTopLevelTypes() {
        file.getTypes().forEach(this::addTopLevelType);
    }

    private void addExternalFunction(ExternFunction function) {
        LLVMTypeRef returnType = getLLVMType(function.returnType());
        LLVMTypeRef[] paramTypes = function.parameters().stream().map(this::getLLVMType).toArray(LLVMTypeRef[]::new);
        PointerPointer<LLVMTypeRef> params = new PointerPointer<>(paramTypes);
        LLVMAddFunction(llvm.module, function.name(), LLVMFunctionType(returnType, params, paramTypes.length, function.varArg() ? 1 : 0));
    }

    private boolean validate() {
        BytePointer error = new BytePointer();
        if (LLVMVerifyModule(llvm.module, LLVMPrintMessageAction, error) != 0) {
            System.err.println("Failed to validate module:\n" + error.getString());
            LLVMDisposeMessage(error);
            return false;
        }
        return true;
    }

    private boolean write() {
        BytePointer error = new BytePointer();
        System.out.println(LLVMPrintModuleToString(llvm.module).getString());
        if (LLVMPrintModuleToFile(llvm.module, output.toString(), error) != 0) {
            System.err.println("Failed to write module:\n" + error.getString());
            LLVMDisposeMessage(error);
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        llvm.close();
    }

    LLVMTypeRef getLLVMType(TypeReference type) {
        switch (type) {
            case PrimitiveTypeReference(Primitive primitive) -> {
                switch (primitive) {
                    case I8, U8 -> {
                        return LLVMInt8TypeInContext(llvm.ctx);
                    }
                    case I16, U16 -> {
                        return LLVMInt16TypeInContext(llvm.ctx);
                    }
                    case I32, U32 -> {
                        return LLVMInt32TypeInContext(llvm.ctx);
                    }
                    case I64, U64 -> {
                        return LLVMInt64TypeInContext(llvm.ctx);
                    }
                    case I128, U128 -> {
                        return LLVMInt128TypeInContext(llvm.ctx);
                    }
                    case BOOL -> {
                        return LLVMInt1TypeInContext(llvm.ctx);
                    }
                    case F32 -> {
                        return LLVMFloatTypeInContext(llvm.ctx);
                    }
                    case F64 -> {
                        return LLVMDoubleTypeInContext(llvm.ctx);
                    }
                    case VOID -> {
                        return LLVMVoidTypeInContext(llvm.ctx);
                    }
                }
            }
            case ArrayTypeReference(TypeReference contained) -> {
                return LLVMPointerType(getLLVMType(contained), 0);
            }
            case NamedTypeReference namedTypeReference ->{
                throw new Error("Unhandled named type reference: " + namedTypeReference.name());
            }
            case null, default -> {
                throw new Error("Unhandled type: " + type);
            }
        }
        throw new Error("Internal error for unhandled type: " + type);
    }
}
