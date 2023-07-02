import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class ImageInsertionAndRenaming extends JFrame {

    JTextField directoryField;
    JTextField positionField;
    JTextField imageField;
    JButton directoryButton;
    JButton imageButton;
    JButton insertButton;

    public ImageInsertionAndRenaming() {
        createView();

        setTitle("Image Insertion and Renaming");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createView() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JLabel directoryLabel = new JLabel("Directory:");
        panel.add(directoryLabel);

        directoryField = new JTextField(50);
        panel.add(directoryField);

        directoryButton = new JButton("Select directory");
        panel.add(directoryButton);
        directoryButton.addActionListener(new DirectoryAction());

        JLabel positionLabel = new JLabel("Insert position:");
        panel.add(positionLabel);

        positionField = new JTextField(10);
        panel.add(positionField);

        JLabel imageLabel = new JLabel("New image:");
        panel.add(imageLabel);

        imageField = new JTextField(50);
        panel.add(imageField);

        imageButton = new JButton("Select image");
        panel.add(imageButton);
        imageButton.addActionListener(new ImageAction());

        insertButton = new JButton("Insert image and rename files");
        panel.add(insertButton);
        insertButton.addActionListener(new InsertAction());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ImageInsertionAndRenaming().setVisible(true);
            }
        });
    }

    private class DirectoryAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    private class ImageAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JPG Images", "jpg"));
            int returnValue = chooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                imageField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    private class InsertAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File directory = new File(directoryField.getText());
            File image = new File(imageField.getText());
            String position = positionField.getText();

            if (!position.matches("\\d+")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for the insert position.");
                return;
            }

            if (!directory.exists()) {
                JOptionPane.showMessageDialog(null, "The selected directory does not exist.");
                return;
            }

            if (!image.exists()) {
                JOptionPane.showMessageDialog(null, "The selected image does not exist.");
                return;
            }

            try {
                insertImage(directory, Integer.parseInt(position), image);
                JOptionPane.showMessageDialog(null, "Image inserted and files renamed successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error inserting image and renaming files.");
            }
        }

        private void insertImage(File directory, int insertPosition, File newImage) throws IOException {
            File[] files = directory.listFiles((d, name) -> name.toLowerCase().endsWith(".jpg"));
            java.util.Arrays.sort(files, (f1, f2) -> Integer.compare(getNumber(f1.getName()), getNumber(f2.getName())));

            for (int i = files.length - 1; i >= 0; i--) {
                int number = getNumber(files[i].getName());
                if (number >= insertPosition) {
                    File dest = new File(directory, (number + 1) + ".jpg");
                    Files.move(files[i].toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            Files.copy(newImage.toPath(), new File(directory, insertPosition + ".jpg").toPath());
        }

        private int getNumber(String name) {
            return Integer.parseInt(name.substring(0, name.lastIndexOf(".")));
        }
    }
}
