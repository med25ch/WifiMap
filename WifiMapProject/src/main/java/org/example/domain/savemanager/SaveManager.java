package org.example.domain.savemanager;

import org.example.domain.savemanager.Project;

import java.io.*;

public class SaveManager {
    private static String lastLoadPath = null;

    public static String getLastLoadPath() {
        return lastLoadPath;
    }

    public static void save(Project project, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            lastLoadPath = path;
            oos.writeObject(project);
        } catch (IOException e) {
        }
    }

    public static Project load(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            lastLoadPath = path;
            return (Project) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public static Project deepCopy(Project original) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (Project) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to deep copy the project", e);
        }
    }
}
