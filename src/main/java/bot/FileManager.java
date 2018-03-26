package bot;

import java.io.*;
import java.util.ArrayList;

import static java.lang.System.getProperty;

public class FileManager {
    private static String configFilePath = getProperty("user.dir") + File.separator + "Bot_data";
    private File configFile;
    private String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
        createConfig();
    }

    public FileManager(String fileName, boolean isUserData) {
        this.fileName = fileName;
        if (isUserData) {
            createConfig(true);
        } else {
            createConfig();
        }

    }

    public boolean removeLineIfCointains (String content) {
        File tempFile = new File (configFilePath, "temp.data");
        BufferedReader reader;
        BufferedWriter writer;
        try {
             reader = new BufferedReader(new FileReader(configFile));
             writer = new BufferedWriter(new FileWriter(tempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }
        String currentLine;
        try {
            while ((currentLine = reader.readLine()) != null) {
                String trimmedLine = currentLine.trim();
                if (trimmedLine.contains(content)) continue;
                writer.write(currentLine + "\n");
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!configFile.delete()) return false;
        if (!tempFile.renameTo(configFile)) return false;
        return true;
    }

    public boolean replaceLine (int line, String input) {
        File tempFile = new File (configFilePath, "temp.data");
        BufferedReader in;
        BufferedWriter out;
        try {
            in = new BufferedReader(new FileReader(configFile));
            out = new BufferedWriter(new FileWriter(tempFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error");
            return false;
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }
        String currentLine;
        try {
            int i = 0;
            while ((currentLine = in.readLine()) != null) {
                if (i == line-1) {
                    out.write(input + "\n");
                } else {
                    out.write(currentLine + "\n");
                }
                i++;
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!configFile.delete()) return false;
        if (!tempFile.renameTo(configFile)) return false;
        return true;
    }

    private void createConfig (boolean isUserData) {
        configFile = new File(configFilePath, fileName + ".data");
        if (configFile.exists()) {
            System.out.println(">>> " + configFile + " already exists, continuing...");
        } else {
            if (!configFile.getParentFile().isDirectory()) {
                configFile.getParentFile().mkdirs();
            }
            try{
                configFile.createNewFile();
                System.out.println(">>> " + configFile + " succesfully created");
                if (isUserData) {
                    this.writeLine("Recently joined users: [ID, Name, DateJoined]");
                    this.writeLine();
                }
            } catch (IOException ex) {
                System.out.println("<<< Error creating config file");
            }
        }
    }

    private void createConfig () {
        configFile = new File(configFilePath, fileName + ".data");
        if (configFile.exists()) {
            System.out.println(">>> " + configFile + " already exists, continuing...");
        } else {
            if (!configFile.getParentFile().isDirectory()) {
                configFile.getParentFile().mkdirs();
            }
            try{
                configFile.createNewFile();
                System.out.println(">>> " + configFile + " succesfully created");
            } catch (IOException ex) {
                System.out.println("<<< Error creating config file");
            }
        }
    }

    public void writeLine (String line) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(configFile, true));
            out.append(line);
            out.newLine();
            out.close();
            System.out.println(">>> Succesfully written to " + configFile);
        } catch (IOException ex) {
            System.out.println("<<< Error writing to config file");
        }
    }

    public void writeLine () {
        writeLine("");
    }

    public ArrayList<Integer> parseNewUsers () {
        ArrayList<Integer> result = new ArrayList<Integer>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            System.out.println("<<< FileNotFoundException --- cannot parse new users");
            return null;
        }
        String currentLine;
        try {
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.length() > 1) {
                    if (currentLine.charAt(0) == '[' && currentLine.charAt(currentLine.length()-1) == ']') {
                        String cache = "";
                        for (int i = 0; i < currentLine.length(); i++) {
                            if (currentLine.charAt(i) == ',') {
                                result.add(Integer.parseInt(cache));
                                break;
                            } else if (currentLine.charAt(i) != '[') cache += currentLine.charAt(i);
                        }
                    }
                }
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getLines (boolean isUserData) {
        ArrayList<String> result = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            System.out.println("<<< FileNotFoundException --- cannot get File-lines");
            return null;
        }
        String currentLine;
        try {
            while ((currentLine = reader.readLine()) != null) {
                if (isUserData) {
                    if (currentLine.length() > 1) {
                        if (currentLine.charAt(0) == '[' && currentLine.charAt(currentLine.length() - 1) == ']') {
                            result.add(currentLine);
                        }
                    }
                }
                else {
                    result.add(currentLine);
                }
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getLines () {
        ArrayList<String> result = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            System.out.println("<<< FileNotFoundException --- cannot get File-lines");
            return null;
        }
        String currentLine;
        try {
            while ((currentLine = reader.readLine()) != null) {
                    result.add(currentLine);
            }
            reader.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
