package main;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.zip.GZIPOutputStream;

public class CompressingThread implements Runnable {
    private Serializable object;
    private byte[] compressedOutput;

    public byte[] getCompressedOutput() {
        return compressedOutput;
    }

    public CompressingThread setCompressedOutput(byte[] compressedOutput) {
        this.compressedOutput = compressedOutput;
        return this;
    }

    public Serializable getObject() {
        return object;
    }

    public CompressingThread setObject(Serializable object) {
        this.object = object;
        return this;
    }

    @Override
    public void run() {
        Instant start = Instant.now();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream zos = new GZIPOutputStream(bos);
            ObjectOutputStream ous = new ObjectOutputStream(zos);

            ous.writeObject(object);
            zos.finish();
            bos.flush();

            this.setCompressedOutput(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(
                "Compressing time: " + Duration.between(start, Instant.now()).toMillis() + " ms.");

    }

    //    @Override
    //    public void run() {
    //        Instant start = Instant.now();
    //
    //        ByteArrayOutputStream bos = new ByteArrayOutputStream();
    //        ObjectOutput out = null;
    //
    //        try {
    //            out = new ObjectOutputStream(bos);
    //            out.writeObject(object);
    //            out.flush();
    //            byte[] objectAsBytes = bos.toByteArray();
    //
    //
    //            byte[] output = new byte[1024];
    //            Deflater deflater = new Deflater();
    //            deflater.setInput(objectAsBytes);
    //            deflater.finish();
    //            deflater.deflate(output);
    //            deflater.end();
    //
    //            this.setCompressedOutput(output);
    //
    //        } catch (Exception e) {
    //
    //        } finally {
    //            try {
    //                bos.close();
    //            } catch (Exception ex) {
    //                // ignore close exception
    //            }
    //        }
    //
    //        System.out.println(
    //                "Compressing time: " + Duration.between(start, Instant.now()).toMillis() + " ms.");
    //
    //    }
}
