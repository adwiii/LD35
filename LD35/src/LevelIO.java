
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LevelIO {
	public static void writeLevel(Level level, String source) {
		try {
			File f = new File(source);
			f.createNewFile();
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(source));
			stream.writeObject(level);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Level readLevel(String source) {
		try {
//			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(source));
			ObjectInputStream stream = new ObjectInputStream(LevelIO.class.getResourceAsStream(source));
			Level level = (Level) stream.readObject();
			stream.close();
			return level;
		} catch (Exception e) {
			return null;
		}
	}
}
