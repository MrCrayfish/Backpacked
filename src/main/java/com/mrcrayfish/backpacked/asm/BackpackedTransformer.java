package com.mrcrayfish.backpacked.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class BackpackedTransformer implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if(bytes == null)
        {
            return null;
        }

        switch(transformedName)
        {
            case "net.minecraft.entity.player.EntityPlayer":
                bytes = this.patch(new MethodEntry("", "<init>", "(Lnet/minecraft/world/World;Lcom/mojang/authlib/GameProfile;)V"), this::patch_EntityPlayer_Init, bytes);
                break;
            case "net.minecraft.client.gui.inventory.GuiContainerCreative":
                bytes = this.patch(new MethodEntry("func_147050_b", "setCurrentCreativeTab", "(Lnet/minecraft/creativetab/CreativeTabs;)V"), this::patch_GuiContainerCreative_setCurrentCreativeTab, bytes);
                break;
            case "net.minecraft.network.NetHandlerPlayServer":
                bytes = this.patch(new MethodEntry("func_147344_a", "processCreativeInventoryAction", "(Lnet/minecraft/network/play/client/CPacketCreativeInventoryAction;)V"), this::patch_NetHandlerPlayServer_processCreativeInventoryAction, bytes);
                break;
            case "net.minecraft.client.gui.inventory.GuiContainer":
                bytes = this.patch(new MethodEntry("func_73863_a", "drawScreen", "(IIF)V"), this::patch_GuiContainer_drawScreen, bytes);
                break;
        }
        return bytes;
    }

    @Nullable
    private MethodNode findMethod(List<MethodNode> methods, MethodEntry entry)
    {
        for(MethodNode node : methods)
        {
            if((node.name.equals(entry.obfName) || node.name.equals(entry.name)) && node.desc.equals(entry.desc))
            {
                return node;
            }
        }
        return null;
    }

    private byte[] patch(MethodEntry entry, Function<MethodNode, Boolean> f, byte[] bytes)
    {
        ClassNode classNode = new ClassNode(Opcodes.ASM5);
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        MethodNode methodNode = this.findMethod(classNode.methods, entry);
        String className = classNode.name.replace("/", ".") + "#" + entry.name + entry.desc;
        if(methodNode != null)
        {
            System.out.println("[Backpacked] Starting to patch: " + className);
            if(f.apply(methodNode))
            {
                System.out.println("[Backpacked] Successfully patched: " + className);
            }
            else
            {
                System.out.println("[Backpacked] Failed to patch: " + className);
            }
        }
        else
        {
            System.out.println("[Backpacked] Failed to find method: " + className);
        }

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private boolean patch_EntityPlayer_Init(MethodNode methodNode)
    {
        AbstractInsnNode foundNode = null;
        for(int i = 0; i < methodNode.instructions.size(); i++)
        {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if(node.getOpcode() != Opcodes.PUTFIELD)
                continue;
            FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
            if(!fieldInsnNode.name.equals("unused180") && !fieldInsnNode.name.equals("field_70741_aB"))
                continue;
            foundNode = node;
            break;
        }
        if(foundNode != null)
        {
            methodNode.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "onPlayerInit", "(Lnet/minecraft/entity/player/EntityPlayer;)V", false));
            methodNode.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
            return true;
        }
        return false;
    }

    private boolean patch_GuiContainerCreative_setCurrentCreativeTab(MethodNode methodNode)
    {
        AbstractInsnNode foundNode = null;
        for(int i = 0; i < methodNode.instructions.size(); i++)
        {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if(node.getOpcode() != Opcodes.GETFIELD)
                continue;
            FieldInsnNode fieldInsnNode = (FieldInsnNode) node;
            if(!fieldInsnNode.name.equals("destroyItemSlot") && !fieldInsnNode.name.equals("field_147064_C"))
                continue;
            if(node.getNext() == null || node.getNext().getOpcode() != Opcodes.INVOKEINTERFACE)
                continue;
            if(node.getNext().getNext() == null || node.getNext().getNext().getOpcode() != Opcodes.POP)
                continue;
            foundNode = node.getNext().getNext();
            break;
        }
        if(foundNode != null)
        {
            methodNode.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "patchCreativeSlots", "(Lnet/minecraft/client/gui/inventory/GuiContainerCreative$ContainerCreative;)V", false));
            methodNode.instructions.insert(foundNode, new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/gui/inventory/GuiContainerCreative$ContainerCreative"));
            methodNode.instructions.insert(foundNode, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainerCreative", remapFieldName("net.minecraft.client.gui.inventory.GuiContainer", "field_147002_h"), "Lnet/minecraft/inventory/Container;"));
            methodNode.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
            return true;
        }
        return false;
    }

    private boolean patch_NetHandlerPlayServer_processCreativeInventoryAction(MethodNode methodNode)
    {
        AbstractInsnNode foundNode = null;
        for(int i = 0; i < methodNode.instructions.size(); i++)
        {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if(node.getOpcode() != Opcodes.INVOKEVIRTUAL)
                continue;
            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
            if(!methodInsnNode.name.equals("getSlotId") && !methodInsnNode.name.equals("func_149627_c"))
                continue;
            AbstractInsnNode nextNode = node.getNext();
            if(nextNode.getOpcode() != Opcodes.BIPUSH)
                continue;
            IntInsnNode var = (IntInsnNode) nextNode;
            if(var.operand != 45)
                continue;
            foundNode = node;
            break;
        }
        if(foundNode != null)
        {
            methodNode.instructions.remove(foundNode.getNext());
            methodNode.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "getCreativeSlotMax", "(Lnet/minecraft/entity/player/EntityPlayerMP;)I", false));
            methodNode.instructions.insert(foundNode, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/NetHandlerPlayServer", remapFieldName("net.minecraft.network.NetHandlerPlayServer", "field_147369_b"), "Lnet/minecraft/entity/player/EntityPlayerMP;"));
            methodNode.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
            return true;
        }
        return false;
    }

    private boolean patch_GuiContainer_drawScreen(MethodNode methodNode)
    {
        AbstractInsnNode foundNode = null;
        for(int i = 0; i < methodNode.instructions.size(); i++)
        {
            AbstractInsnNode node = methodNode.instructions.get(i);
            if(node.getOpcode() != Opcodes.INVOKEVIRTUAL)
                continue;
            MethodInsnNode methodInsnNode = (MethodInsnNode) node;
            if(!methodInsnNode.name.equals("drawGuiContainerBackgroundLayer") && !methodInsnNode.name.equals("func_146976_a"))
                continue;
            foundNode = node;
            break;
        }
        if(foundNode != null)
        {
            methodNode.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "drawBackgroundLayer", "(Lnet/minecraft/client/gui/inventory/GuiContainer;)V", false));
            methodNode.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
            return true;
        }
        return false;
    }

    private static String remapFieldName(String className, String fieldName)
    {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(className.replace('.', '/'));
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, fieldName, null);
    }

    public static class MethodEntry
    {
        private String obfName;
        private String name;
        private String desc;

        public MethodEntry(String obfName, String name, String desc)
        {
            this.obfName = obfName;
            this.name = name;
            this.desc = desc;
        }
    }
}
