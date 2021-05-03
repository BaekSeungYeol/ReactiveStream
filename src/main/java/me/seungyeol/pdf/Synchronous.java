package me.seungyeol.pdf;

import org.springframework.util.FileCopyUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

//public class Synchronous implements Reader {
//
//    @Override
//    public void read(File file, Consumer<Bytes> consumer, Runnable f) throws IOException {
//
//        try(FileInputStream in = new FileInputStream(file)) {
//            byte[] data = new byte[FileCopyUtils.BUFFER_SIZE];
//            int res;
//            while((res = in.read(data,0,data.length)) != -1){
//                consumer.accept(Bytes.from(data,res));
//            }
//            f.run();
//        }
//    }
//}
