function initializeCoreMod() {
	return {
		'player_init': {
		    'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.entity.player.PlayerEntity',
                'methodName': '<init>',
                'methodDesc': '(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/authlib/GameProfile;)V'
            },
			'transformer': function(method) {
			    log("Patching PlayerEntity#<init>");
                patch_PlayerEntity_Init(method);
				return method;
			}
		},
		'creative_inventory_slot': {
		    'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.gui.screen.inventory.CreativeScreen',
                'methodName': 'func_147050_b',
                'methodDesc': '(Lnet/minecraft/item/ItemGroup;)V'
            },
            'transformer': function(method) {
                log("Patching CreativeScreen#func_147050_b");
                patch_CreativeScreen_setCurrentCreativeTab(method);
                return method;
            }
		},
		'creative_inventory_action_fix': {
		    'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.network.play.ServerPlayNetHandler',
                'methodName': 'func_147344_a',
                'methodDesc': '(Lnet/minecraft/network/play/client/CCreativeInventoryActionPacket;)V'
            },
		    'transformer': function(method) {
		        log("Patching ServerPlayerNetHandler#func_147344_a");
		        patch_ServerPlayerNetHandler_processCreativeInventoryAction(method);
                return method;
		    }
		}
	};
}

var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var IntInsnNode = Java.type('org.objectweb.asm.tree.IntInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function patch_PlayerEntity_Init(method) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if(node.getOpcode() == Opcodes.PUTFIELD && node.name.equals(ASMAPI.mapField("field_70741_aB"))) {
            foundNode = node;
            break;
        }
    }
    if(foundNode !== null) {
        method.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "onPlayerInit", "(Lnet/minecraft/entity/player/PlayerEntity;)V", false));
        method.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
        log("Successfully patched PlayerEntity#<init>");
        return;
    }
}

function patch_CreativeScreen_setCurrentCreativeTab(method) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if(node.getOpcode() != Opcodes.GETFIELD || !node.name.equals(ASMAPI.mapField("field_147064_C")))
            continue;
        if(node.getNext() === null || node.getNext().getOpcode() != Opcodes.INVOKEINTERFACE)
            continue;
        if(node.getNext().getNext() === null || node.getNext().getNext().getOpcode() != Opcodes.POP)
            continue;
        foundNode = node.getNext().getNext();
        break;
    }
    if(foundNode !== null) {
        method.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "patchCreativeSlots", "(Lnet/minecraft/client/gui/screen/inventory/CreativeScreen$CreativeContainer;)V", false));
        method.instructions.insert(foundNode, new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/gui/screen/inventory/CreativeScreen$CreativeContainer"));
        method.instructions.insert(foundNode, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screen/inventory/CreativeScreen", ASMAPI.mapField("field_147002_h"), "Lnet/minecraft/inventory/container/Container;"));
        method.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
        log("Successfully patched CreativeScreen#func_147050_b");
        return;
    }
}

function patch_ServerPlayerNetHandler_processCreativeInventoryAction(method) {
    var foundNode = null;
    var instructions = method.instructions.toArray();
    var length = instructions.length;
    for (var i = 0; i < length; i++) {
        var node = instructions[i];
        if(node.getOpcode() == Opcodes.INVOKEVIRTUAL && node.name.equals(ASMAPI.mapMethod("func_149627_c"))) {
            var nextNode = node.getNext();
            if(nextNode.getOpcode() == Opcodes.BIPUSH && nextNode.operand == 45) {
                foundNode = node;
                break;
            }
        }
    }
    if(foundNode !== null) {
        method.instructions.remove(foundNode.getNext());
        method.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "getCreativeSlotMax", "(Lnet/minecraft/entity/player/ServerPlayerEntity;)I", false));
        method.instructions.insert(foundNode, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/network/play/ServerPlayNetHandler", ASMAPI.mapField("field_147369_b"), "Lnet/minecraft/entity/player/ServerPlayerEntity;"));
        method.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
        log("Successfully patched ServerPlayerNetHandler#func_147344_a");

        return;
    }
}

function log(s) {
    print("[backpacked-transformer.js] " + s);
}