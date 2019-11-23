function initializeCoreMod() {
	return {
		'coremodone': {
			'target': {
				'type': 'CLASS',
				'name': 'net.minecraft.entity.player.PlayerEntity'
			},
			'transformer': function(classNode) {
			    print("Patching PlayerEntity...");
                patch({
                    obfName: "",
                    name: "<init>",
                    desc: "(Lnet/minecraft/world/World;Lcom/mojang/authlib/GameProfile;)V",
                    patch: patch_PlayerEntity_Init
                }, classNode);
				return classNode;
			}
		}
	};
}

function findMethod(methods, entry) {
    var length = methods.length;
    for(var i = 0; i < length; i++) {
        var method = methods[i];
        if((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {
            return method;
        }
    }
    return null;
}

function patch(entry, classNode) {
    var method = findMethod(classNode.methods, entry);
    var name = classNode.name.replace("/", ".") + "#" + entry.name + entry.desc;
    if(method !== null) {
        log("Starting to patch: " + name);
        if(entry.patch(method)) {
            log("Successfully patched: " + name);
        } else {
            log("Failed to patch: " + name);
        }
    } else {
        log("Failed to find method: " + name);
    }
}

var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
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
        if(node.getOpcode() == Opcodes.PUTFIELD && node.name.equals("unused180")) {
            foundNode = node;
            break;
        }
    }
    if(foundNode !== null) {
        method.instructions.insert(foundNode, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/mrcrayfish/backpacked/Backpacked", "onPlayerInit", "(Lnet/minecraft/entity/player/PlayerEntity;)V", false));
        method.instructions.insert(foundNode, new VarInsnNode(Opcodes.ALOAD, 0));
        return true;
    }
    return false;
}

function log(s) {
    print("[backpacked-transformer.js] " + s);
}