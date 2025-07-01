package pro.trevor.joe.program.analyzer;

import org.bytedeco.llvm.LLVM.LLVMValueRef;
import pro.trevor.joe.program.File;
import pro.trevor.joe.program.TopLevelType;
import pro.trevor.joe.program.program_class.Class;
import pro.trevor.joe.program.type.TypeReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TypeContext {
    private final Map<String, TopLevelType> types;
    private final Map<String, TypeReference> locals;
    private final Map<String, LLVMValueRef> localStorage;

    public TypeContext(File file) {
        this.types = new HashMap<>();
        this.locals = new HashMap<>();
        this.localStorage = new HashMap<>();
        handleIntrinsicTypes();
        handleTopLevelTypes(file);
    }

    private void handleIntrinsicTypes() {
        this.types.put(Class.UNIVERSAL_PARENT.getName().name(), Class.UNIVERSAL_PARENT);
    }

    private void handleTopLevelTypes(File file) {
        file.getTypes().forEach((type) -> this.types.put(type.getName().name(), type));
    }

    public Optional<TopLevelType> getTopLevelType(String name) {
        return Optional.ofNullable(this.types.get(name));
    }

    public Optional<TypeReference> getLocalType(String name) {
        return Optional.ofNullable(this.locals.get(name));
    }

    public Optional<LLVMValueRef> getLocalStorage(String name) {
        return Optional.ofNullable(this.localStorage.get(name));
    }

    public void addLocalType(String name, TypeReference type) {
        this.locals.put(name, type);
    }

    public void addLocalStorageValue(String name, LLVMValueRef type) {
        this.localStorage.put(name, type);
    }

    public void resetLocals() {
        this.locals.clear();
        this.localStorage.clear();
    }
}
