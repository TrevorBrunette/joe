package pro.trevor.joe.program.llvm;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import pro.trevor.joe.program.type.Primitive;
import pro.trevor.joe.program.type.PrimitiveTypeReference;
import pro.trevor.joe.program.type.TypeReference;

import static org.bytedeco.llvm.global.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllAsmParsers;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllAsmPrinters;
import static org.bytedeco.llvm.global.LLVM.LLVMInitializeAllTargetMCs;

public class LlvmInstance implements AutoCloseable {

    private static boolean LLVM_INITIALIZED = false;

    public final LLVMContextRef ctx;
    public final LLVMModuleRef module;
    public final LLVMBuilderRef builder;

    final String triple;
    final LLVMTargetRef target;
    final LLVMTargetMachineRef targetMachine;
    final LLVMTargetDataRef targetData;
    final TypeReference ptrSize;

    public LlvmInstance(String module, String triple) {
        initializeLlvm();

        this.ctx = LLVMContextCreate();
        this.module = LLVMModuleCreateWithNameInContext(module, this.ctx);
        this.builder = LLVMCreateBuilderInContext(this.ctx);

        this.triple = triple;
        this.target = initializeTarget(this.triple);
        this.targetMachine = initializeTargetMachine(this.target, this.triple);
        this.targetData = initializeTargetData(this.targetMachine);
        this.ptrSize = initializeTargetPtrType(this.targetData);

        System.out.println("Compiling for target triple: '" + this.triple + "'");
    }

    public LlvmInstance(String module) {
        this(module, getSystemLlvmTriple());
    }

    private static void initializeLlvm() {
        if (!LLVM_INITIALIZED) {
            initializeTargets();
            LLVM_INITIALIZED = true;
        }
    }

    private static void initializeTargets() {
        LLVMInitializeAllTargetInfos();
        LLVMInitializeAllTargets();
        LLVMInitializeAllTargetMCs();
        LLVMInitializeAllAsmParsers();
        LLVMInitializeAllAsmPrinters();
    }

    private static String getSystemLlvmTriple() {
        return LLVMGetDefaultTargetTriple().getString();
    }

    private LLVMTargetRef initializeTarget(String triple) {
        PointerPointer<LLVMTargetRef> targetPointer = new PointerPointer<>(new LLVMTargetRef[1]);
        BytePointer bytePointer = new BytePointer();
        LLVMGetTargetFromTriple(triple, targetPointer, bytePointer);
        if (!bytePointer.isNull()) {
            throw new IllegalStateException(bytePointer.getString());
        }
        return targetPointer.get(LLVMTargetRef.class);
    }

    private LLVMTargetMachineRef initializeTargetMachine(LLVMTargetRef target, String triple) {
        return LLVMCreateTargetMachine(target, triple, "generic", "", LLVMCodeGenLevelDefault, LLVMRelocDefault, LLVMCodeModelDefault);
    }

    private LLVMTargetDataRef initializeTargetData(LLVMTargetMachineRef targetMachine) {
        return LLVMCreateTargetDataLayout(targetMachine);
    }

    private PrimitiveTypeReference initializeTargetPtrType(LLVMTargetDataRef targetData) {
        int ptrSizeInBytes = LLVMPointerSize(targetData);
        return new PrimitiveTypeReference(switch (ptrSizeInBytes) {
            case 2 -> Primitive.U16;
            case 4 -> Primitive.U32;
            case 8 -> Primitive.U64;
            case 16 -> Primitive.U8;
            default -> throw new IllegalStateException("Unexpected system pointer size: " + ptrSizeInBytes + " bytes");
        });
    }

    @Override
    public void close() {
        LLVMDisposeTargetMachine(this.targetMachine);
        LLVMDisposeTargetData(this.targetData);
        LLVMDisposeBuilder(this.builder);
        LLVMDisposeModule(this.module);
        LLVMContextDispose(this.ctx);
    }
}
