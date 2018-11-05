import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
    }
}
