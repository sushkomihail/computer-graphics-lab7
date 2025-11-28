import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class Window extends JFrame {
    private final ImageScaler imageScaler;

    private JLabel baseImageLabel;
    private JLabel scaledImageLabel;
    private JSlider scaleSlider;
    private JComboBox<String> methodComboBox;

    public Window() {
        imageScaler = new ImageScaler();
        createUi();
    }

    private void createUi() {
        tryApplySystemTheme();

        setTitle("Lab 7");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // View panel
        JPanel viewPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        baseImageLabel = new JLabel();
        baseImageLabel.setBorder(BorderFactory.createTitledBorder("Оригинальное изображение"));
        JScrollPane originalImageScrollPane = new JScrollPane(baseImageLabel);
        viewPanel.add(originalImageScrollPane);

        scaledImageLabel = new JLabel();
        scaledImageLabel.setBorder(BorderFactory.createTitledBorder("Масштабированное изображение"));
        JScrollPane scaledImageScrollPane = new JScrollPane(scaledImageLabel);
        viewPanel.add(scaledImageScrollPane);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton loadButton = new JButton("Загрузить изображение");
        loadButton.addActionListener(e -> loadImage());
        controlPanel.add(loadButton);

        JButton testShapesButton = new JButton("Тестовые фигуры");
        testShapesButton.addActionListener(e -> {
            BufferedImage image = generateImageFromShapes();
            imageScaler.setBaseImage(image);
            displayImage(image, baseImageLabel);
            updateScaledImageLabel();
        });
        controlPanel.add(testShapesButton);

        JPanel methodPanel = createMethodPanel();
        controlPanel.add(methodPanel);

        JPanel scalePanel = createScalePanel();
        controlPanel.add(scalePanel);

        add(viewPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);

        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private JPanel createScalePanel() {
        JPanel scalePanel = new JPanel();
        scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));

        JLabel scaleLabel = new JLabel("Масштаб (100%)");
        scaleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scalePanel.add(scaleLabel);

        scaleSlider = new JSlider(0, 400, 100);
        var labelTable = scaleSlider.createStandardLabels(100, 0);
        scaleSlider.setLabelTable(labelTable);
        scaleSlider.setPaintTicks(true);
        scaleSlider.setPaintLabels(true);
        scaleSlider.addChangeListener(e -> {
            scaleLabel.setText(String.format("Масштаб (%d%%)", scaleSlider.getValue()));
            updateScaledImageLabel();
        });
        scalePanel.add(scaleSlider);
        return scalePanel;
    }

    private JPanel createMethodPanel() {
        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new BoxLayout(methodPanel, BoxLayout.Y_AXIS));

        JLabel methodLabel = new JLabel("Метод");
        methodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        methodPanel.add(methodLabel);

        JComboBox<String> methodComboBox = createMethodComboBox();
        methodPanel.add(methodComboBox);
        return methodPanel;
    }

    private JComboBox<String> createMethodComboBox() {
        methodComboBox = new JComboBox<>(new String[]{
                ImageScaler.NEAREST_NEIGHBOR,
                ImageScaler.BICUBIC
        });
        methodComboBox.addActionListener(e -> updateScaledImageLabel());
        return methodComboBox;
    }

    private void loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images", "jpg", "jpeg", "png", "bmp"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedImage image = ImageIO.read(file);
                imageScaler.setBaseImage(image);
                displayImage(image, baseImageLabel);
                updateScaledImageLabel();
            } catch (Exception exception) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки изображения: " + exception.getMessage());
            }
        }
    }

    private BufferedImage generateImageFromShapes() {
        int width = 400;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(new Color(255, 203, 101));
        g2d.fillOval(50, 50, 100, 100);

        g2d.setStroke(new BasicStroke(5));
        g2d.drawOval(200, 50, 150, 150);
        g2d.drawOval(220, 70, 110, 110);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawLine(0, 0, width, height);
        g2d.drawLine(width, 0, 0, height);

        g2d.setStroke(new BasicStroke(3));

        for (int i = 0; i < 2; i++) {
            g2d.drawLine(50, 250 + i * 15, 350, 250 + i * 15);
        }

        g2d.dispose();
        return image;
    }

    private void updateScaledImageLabel() {
        String method = (String) Objects.requireNonNull(methodComboBox.getSelectedItem());
        imageScaler.updateScale(method, scaleSlider.getValue());
        displayImage(imageScaler.getScaledImage(), scaledImageLabel);
    }

    private void displayImage(BufferedImage image, JLabel imageLabel) {
        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
        }
    }

    private void tryApplySystemTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
