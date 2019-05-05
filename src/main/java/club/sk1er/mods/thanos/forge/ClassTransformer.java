package club.sk1er.mods.thanos.forge;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collection;


public final class ClassTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger("ASM");


    private final Multimap<String, ThanosTransformer> transformerMap = ArrayListMultimap.create();

    public ClassTransformer() {
        this.registerTransformer(new RenderGlobalTransformer());

    }




    private void registerTransformer(ThanosTransformer transformer) {
        for (String cls : transformer.getClassNames()) {
            this.transformerMap.put(cls, transformer);
        }
    }


    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null)
            return null;





        Collection<ThanosTransformer> transformers = this.transformerMap.get(transformedName);
        if (transformers.isEmpty())
            return bytes;

        LOGGER.info("Found {} transformers for {}", transformers.size(), transformedName);

        // initialize class reader
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        // apply transformers
        transformers.forEach(transformer -> transformer.transform(classNode, transformedName));

        // write new class bytes
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        try {
            classNode.accept(classWriter);
        } catch (Throwable e) {
            LOGGER.error("Exception when transforming {} : {}", transformedName, e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return classWriter.toByteArray();
    }

}