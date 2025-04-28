import java.io.*;
import java.util.List;

public class GraphObjectIO<T extends Serializable> {

    // Write a list of objects to a file
    public void writeListToFile(List<T> list, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
            System.out.println("List successfully written to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing list to file: " + e.getMessage());
        }
    }

    // Read a list of objects from a file
    @SuppressWarnings("unchecked")
    public List<T> readListFromFile(String filename) {
        List<T> list = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            list = (List<T>) ois.readObject();
            System.out.println("List successfully read from " + filename);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading list from file: " + e.getMessage());
        }
        return list;
    }
}
