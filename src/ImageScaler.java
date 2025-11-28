//import javax.swing.*;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.image.BufferedImage;
//import java.awt.image.DataBufferInt;
//import java.io.File;
//
//public class CustomImageScaler extends JFrame {
//    private BufferedImage originalImage;
//    private BufferedImage scaledImage;
//
//    public CustomImageScaler() {
//        initializeUI();
//        setupEventListeners();
//        createTestImage();
//    }
//
//    private void initializeUI() {
//
//    }
//
//    private void setupEventListeners() {
//        loadButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                loadImage();
//            }
//        });
//
//        testShapesButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                createTestImage();
//            }
//        });
//
//        scaleSlider.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                updateScale();
//            }
//        });
//
//        methodComboBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                updateScale();
//            }
//        });
//    }
//
//    private void createTestImage() {
//        int width = 400;
//        int height = 400;
//        originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics = originalImage.createGraphics();
//
//        graphics.setColor(Color.WHITE);
//        graphics.fillRect(0, 0, width, height);
//
//        graphics.setColor(Color.RED);
//        graphics.fillOval(50, 50, 100, 100);
//        graphics.setColor(Color.BLACK);
//        graphics.drawOval(50, 50, 100, 100);
//
//        graphics.setColor(Color.BLUE);
//        graphics.setStroke(new BasicStroke(5));
//        graphics.drawOval(200, 50, 150, 150);
//        graphics.drawOval(220, 70, 110, 110);
//
//        graphics.setColor(Color.GREEN);
//        graphics.setStroke(new BasicStroke(3));
//        for (int i = 0; i < 10; i++) {
//            graphics.drawLine(50, 200 + i * 15, 350, 200 + i * 15);
//        }
//
//        graphics.setColor(Color.BLACK);
//        graphics.setStroke(new BasicStroke(2));
//        graphics.drawLine(0, 0, width, height);
//        graphics.drawLine(width, 0, 0, height);
//
//        graphics.dispose();
//        displayOriginalImage();
//        updateScale();
//    }
//
//    private void displayOriginalImage() {
//        if (originalImage != null) {
//            ImageIcon icon = new ImageIcon(scaleForDisplay(originalImage, 300, 300));
//            originalLabel.setIcon(icon);
//            originalLabel.setText("Оригинал: " + originalImage.getWidth() + "x" + originalImage.getHeight());
//        }
//    }
//
//    private void updateScale() {
//        if (originalImage == null) return;
//
//        int scalePercent = scaleSlider.getValue();
//        scaleValueLabel.setText(scalePercent + "%");
//
//        double scaleFactor = scalePercent / 100.0;
//        int newWidth = (int)(originalImage.getWidth() * scaleFactor);
//        int newHeight = (int)(originalImage.getHeight() * scaleFactor);
//
//        String method = (String) methodComboBox.getSelectedItem();
//
//        long startTime = System.nanoTime();
//
//        assert method != null;
//
//        if (method.equals(NEAREST_NEIGHBOR)) {
//            scaledImage = nearestNeighborScale(originalImage, newWidth, newHeight);
//        } else {
//            scaledImage = bicubicScale(originalImage, newWidth, newHeight);
//        }
//
//        long endTime = System.nanoTime();
//        long duration = (endTime - startTime) / 1000000;
//
//        BufferedImage displayImage = scaleForDisplay(scaledImage, 300, 300);
//        ImageIcon icon = new ImageIcon(displayImage);
//        scaledLabel.setIcon(icon);
//        scaledLabel.setText("Масштаб: " + scalePercent + "%, " + newWidth + "x" + newHeight + " (" + duration + "мс)");
//
//        analyzeQuality(duration);
//    }
//
//    private BufferedImage nearestNeighborScale(BufferedImage original, int newWidth, int newHeight) {
//        if (newWidth <= 0 || newHeight <= 0) {
//            return original;
//        }
//
//        BufferedImage scaled = new BufferedImage(newWidth, newHeight, original.getType());
//        int[] originalPixels = getPixels(original);
//        int[] scaledPixels = getPixels(scaled);
//
//        int origWidth = original.getWidth();
//        int origHeight = original.getHeight();
//
//        double scaleX = (double) origWidth / newWidth;
//        double scaleY = (double) origHeight / newHeight;
//
//        for (int y = 0; y < newHeight; y++) {
//            for (int x = 0; x < newWidth; x++) {
//                int origX = (int)(x * scaleX);
//                int origY = (int)(y * scaleY);
//
//                // Обеспечиваем границы
//                origX = Math.min(origX, origWidth - 1);
//                origY = Math.min(origY, origHeight - 1);
//
//                int color = originalPixels[origY * origWidth + origX];
//                scaledPixels[y * newWidth + x] = color;
//            }
//        }
//
//        setPixels(scaled, scaledPixels);
//        return scaled;
//    }
//
//    private BufferedImage bicubicScale(BufferedImage original, int newWidth, int newHeight) {
//        if (newWidth <= 0 || newHeight <= 0) {
//            return original;
//        }
//
//        BufferedImage scaled = new BufferedImage(newWidth, newHeight, original.getType());
//        int[] originalPixels = getPixels(original);
//        int[] scaledPixels = getPixels(scaled);
//
//        int origWidth = original.getWidth();
//        int origHeight = original.getHeight();
//
//        double scaleX = (double) origWidth / newWidth;
//        double scaleY = (double) origHeight / newHeight;
//
//        for (int y = 0; y < newHeight; y++) {
//            for (int x = 0; x < newWidth; x++) {
//                double origX = x * scaleX;
//                double origY = y * scaleY;
//
//                int color = bicubicInterpolate(originalPixels, origWidth, origHeight, origX, origY);
//                scaledPixels[y * newWidth + x] = color;
//            }
//        }
//
//        setPixels(scaled, scaledPixels);
//        return scaled;
//    }
//
//    private int bicubicInterpolate(int[] pixels, int width, int height, double x, double y) {
//        int xFloor = (int) Math.floor(x);
//        int yFloor = (int) Math.floor(y);
//
//        double xFraction = x - xFloor;
//        double yFraction = y - yFloor;
//
//        double[] reds = new double[4];
//        double[] greens = new double[4];
//        double[] blues = new double[4];
//
//        for (int j = -1; j <= 2; j++) {
//            double[] rowRed = new double[4];
//            double[] rowGreen = new double[4];
//            double[] rowBlue = new double[4];
//
//            for (int i = -1; i <= 2; i++) {
//                int sampleX = clamp(xFloor + i, 0, width - 1);
//                int sampleY = clamp(yFloor + j, 0, height - 1);
//
//                int color = pixels[sampleY * width + sampleX];
//                rowRed[i + 1] = (color >> 16) & 0xFF;
//                rowGreen[i + 1] = (color >> 8) & 0xFF;
//                rowBlue[i + 1] = color & 0xFF;
//            }
//
//            reds[j + 1] = cubicInterpolate(rowRed, xFraction);
//            greens[j + 1] = cubicInterpolate(rowGreen, xFraction);
//            blues[j + 1] = cubicInterpolate(rowBlue, xFraction);
//        }
//
//        int r = clamp((int) cubicInterpolate(reds, yFraction), 0, 255);
//        int g = clamp((int) cubicInterpolate(greens, yFraction), 0, 255);
//        int b = clamp((int) cubicInterpolate(blues, yFraction), 0, 255);
//
//        return (r << 16) | (g << 8) | b;
//    }
//
//    private double cubicInterpolate(double[] p, double x) {
//        return p[1] + 0.5 * x * (p[2] - p[0] +
//                x * (2.0 * p[0] - 5.0 * p[1] + 4.0 * p[2] - p[3] +
//                        x * (3.0 * (p[1] - p[2]) + p[3] - p[0])));
//    }
//
//    private int clamp(int value, int min, int max) {
//        return Math.max(min, Math.min(max, value));
//    }
//
//    private int[] getPixels(BufferedImage image) {
//        if (image.getRaster().getDataBuffer() instanceof DataBufferInt) {
//            return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
//        }
//
//        int width = image.getWidth();
//        int height = image.getHeight();
//        int[] pixels = new int[width * height];
//        image.getRGB(0, 0, width, height, pixels, 0, width);
//        return pixels;
//    }
//
//    private void setPixels(BufferedImage image, int[] pixels) {
//        if (image.getRaster().getDataBuffer() instanceof DataBufferInt) {
//            int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
//            System.arraycopy(pixels, 0, data, 0, Math.min(pixels.length, data.length));
//        } else {
//            image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
//        }
//    }
//
//    private BufferedImage scaleForDisplay(BufferedImage original, int maxWidth, int maxHeight) {
//        double scale = Math.min((double) maxWidth / original.getWidth(),
//                (double) maxHeight / original.getHeight());
//        int newWidth = (int)(original.getWidth() * scale);
//        int newHeight = (int)(original.getHeight() * scale);
//
//        return nearestNeighborScale(original, newWidth, newHeight);
//    }
//
//    private void analyzeQuality(long duration) {
//        if (originalImage == null || scaledImage == null) return;
//
//        String method = (String) methodComboBox.getSelectedItem();
//        double scaleFactor = scaleSlider.getValue() / 100.0;
//
//        StringBuilder analysis = new StringBuilder();
//        analysis.append("=== Анализ качества ===\n");
//        analysis.append("Метод: ").append(method).append("\n");
//        analysis.append("Время выполнения: ").append(duration).append(" мс\n");
//        analysis.append("Коэффициент масштабирования: ").append(String.format("%.2f", scaleFactor)).append("\n");
//        analysis.append("Оригинальный размер: ").append(originalImage.getWidth()).append("x").append(originalImage.getHeight()).append("\n");
//        analysis.append("Новый размер: ").append(scaledImage.getWidth()).append("x").append(scaledImage.getHeight()).append("\n\n");
//
//        double originalSharpness = calculateSharpness(originalImage);
//        double scaledSharpness = calculateSharpness(scaledImage);
//        analysis.append("Резкость оригинала: ").append(String.format("%.4f", originalSharpness)).append("\n");
//        analysis.append("Резкость результата: ").append(String.format("%.4f", scaledSharpness)).append("\n");
//        analysis.append("Относительная резкость: ").append(String.format("%.4f", scaledSharpness / originalSharpness)).append("\n\n");
//
//        if (scaleFactor > 1.0) {
//            analysis.append("УВЕЛИЧЕНИЕ изображения\n");
//            if (method.equals(NEAREST_NEIGHBOR)) {
//                analysis.append("• Быстрое выполнение\n");
//                analysis.append("• Пикселизация и ступенчатые края\n");
//                analysis.append("• Сохранение четких границ\n");
//                analysis.append("• Идеально для пиксельной графики\n");
//            } else {
//                analysis.append("• Более медленное выполнение\n");
//                analysis.append("• Плавные переходы и сглаженные края\n");
//                analysis.append("• Лучшее качество для фотографий\n");
//                analysis.append("• Может вызывать размытие\n");
//            }
//        } else {
//            analysis.append("УМЕНЬШЕНИЕ изображения\n");
//            if (method.equals(NEAREST_NEIGHBOR)) {
//                analysis.append("• Потеря мелких деталей\n");
//                analysis.append("• Алиасинг (ступенчатость)\n");
//                analysis.append("• Муаровые узоры\n");
//                analysis.append("• Быстрая обработка\n");
//            } else {
//                analysis.append("• Сохранение плавности линий\n");
//                analysis.append("• Минимальный алиасинг\n");
//                analysis.append("• Лучшее сохранение деталей\n");
//                analysis.append("• Требует больше вычислений\n");
//            }
//        }
//
//        analysisTextArea.setText(analysis.toString());
//    }
//
//    private double calculateSharpness(BufferedImage image) {
//        double totalGradient = 0;
//        int count = 0;
//        int[] pixels = getPixels(image);
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        for (int y = 1; y < height - 1; y++) {
//            for (int x = 1; x < width - 1; x++) {
//                int center = pixels[y * width + x];
//                int right = pixels[y * width + (x + 1)];
//                int bottom = pixels[(y + 1) * width + x];
//
//                double gradX = colorDifference(center, right);
//                double gradY = colorDifference(center, bottom);
//
//                totalGradient += Math.sqrt(gradX * gradX + gradY * gradY);
//                count++;
//            }
//        }
//
//        return count > 0 ? totalGradient / count : 0;
//    }
//
//    private double colorDifference(int rgb1, int rgb2) {
//        int r1 = (rgb1 >> 16) & 0xFF;
//        int g1 = (rgb1 >> 8) & 0xFF;
//        int b1 = rgb1 & 0xFF;
//
//        int r2 = (rgb2 >> 16) & 0xFF;
//        int g2 = (rgb2 >> 8) & 0xFF;
//        int b2 = rgb2 & 0xFF;
//
//        return Math.sqrt((r1 - r2) * (r1 - r2) +
//                (g1 - g2) * (g1 - g2) +
//                (b1 - b2) * (b1 - b2));
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new CustomImageScaler().setVisible(true);
//            }
//        });
//    }
//}