package pro.trevor.joe.program.extern;

import pro.trevor.joe.parser.tree.declaration.Access;
import pro.trevor.joe.program.type.TypeReference;

import java.util.List;

public record ExternFunction(Access access, ExternVariant variant, String name, List<TypeReference> parameters, TypeReference returnType) {

    public String toExternalLinkageString() throws ExternGenerationException {

        switch (variant) {
            case C -> {
                StringBuilder result = new StringBuilder();
                result.append(variant.toExternalLinkageString(returnType));
                result.append(" ");
                result.append(name);
                result.append("(");
                for (int i = 0; i < parameters.size(); i++) {
                    result.append(variant.toExternalLinkageString(parameters.get(i)));
                    if (i < parameters.size() - 1) {
                        result.append(", ");
                    }
                }
                result.append(");");
                return result.toString();
            }
        }
        throw new ExternGenerationException(variant, "Unexpected fallthrough");
    }

}
