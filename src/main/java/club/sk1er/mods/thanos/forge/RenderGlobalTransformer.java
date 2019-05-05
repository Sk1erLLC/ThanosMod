package club.sk1er.mods.thanos.forge;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class RenderGlobalTransformer implements ThanosTransformer {
    @Override
    public String[] getClassNames() {
        return new String[]{"net.minecraft.client.renderer.RenderGlobal"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            String methodName = mapMethodName(classNode, method);
            if (methodName.equalsIgnoreCase("onEntityRemoved") || methodName.equalsIgnoreCase("func_72847_b")) {
                method.instructions.insertBefore(method.instructions.getFirst(), getCallInsn());
            }
        }
    }

    private InsnList getCallInsn() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "club/sk1er/mods/thanos/ThanosMod", "onEntityRemoved", "(Lnet/minecraft/entity/Entity;)V", false));
        return list;
    }
}
