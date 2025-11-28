import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageScaler {
    public static final String NEAREST_NEIGHBOR = "Интерполяция методом ближайшего соседа";
    public static final String BICUBIC = "Бикубическая интерполяция";

    private BufferedImage baseImage;
    private BufferedImage scaledImage;

    public void setBaseImage(BufferedImage image) {
        baseImage = image;
    }

    public BufferedImage getScaledImage() {
        return scaledImage;
    }

    public void updateScale(String method, int scale) {
        if (baseImage == null) return;

        switch (method) {
            case NEAREST_NEIGHBOR:
                interpolateByNearestNeighbourMethod(scale);
                break;
            case BICUBIC:
                interpolateByBicubicMethod(scale);
                break;
        }
    }

    private void interpolateByNearestNeighbourMethod(int scale) {
        if (baseImage == null) return;

        if (scale == 0) {
            scaledImage = copyImage(baseImage);
            return;
        }

        float scaleFactor = (float) scale / 100;
        int scaledWidth = (int) (baseImage.getWidth() * scaleFactor);
        int scaledHeight = (int) (baseImage.getHeight() * scaleFactor);
        scaledImage = new BufferedImage(scaledWidth, scaledHeight, baseImage.getType());

        for (int y = 0; y < scaledHeight; y++) {
            for (int x = 0; x < scaledWidth; x++) {
                int baseX = (int)(x / scaleFactor);
                int baseY = (int)(y / scaleFactor);
                scaledImage.setRGB(x, y, baseImage.getRGB(baseX, baseY));
            }
        }
    }

    private void interpolateByBicubicMethod(int scale) {
        if (baseImage == null) return;

        if (scale == 0) {
            scaledImage = copyImage(baseImage);
            return;
        }

        float scaleFactor = (float) scale / 100;
        int scaledWidth = (int) (baseImage.getWidth() * scaleFactor);
        int scaledHeight = (int) (baseImage.getHeight() * scaleFactor);
        scaledImage = new BufferedImage(scaledWidth, scaledHeight, baseImage.getType());

        for (int y = 0; y < scaledHeight; y++) {
            for (int x = 0; x < scaledWidth; x++) {
                float baseX = x / scaleFactor;
                float baseY = y / scaleFactor;
                Color color = getColorByBicubicFunction(baseX, baseY);
                scaledImage.setRGB(x, y, color.getRGB());
            }
        }
    }

    private Color getColorByBicubicFunction(float x, float y) {
        double relativeX = x - Math.floor(x);
        double relativeY = y - Math.floor(y);

        int[][] reds = new int[4][4];
        int[][] greens = new int[4][4];
        int[][] blues = new int[4][4];

        for (int i = -1; i <= 2; i++) {
            for (int j = -1; j <= 2; j++) {
                int pointX = clamp((int)x + j, 0, baseImage.getWidth() - 1);
                int pointY = clamp((int)y + i, 0, baseImage.getHeight() - 1);
                Color color = new Color(baseImage.getRGB(pointX, pointY));
                reds[i + 1][j + 1] = color.getRed();
                greens[i + 1][j + 1] = color.getGreen();
                blues[i + 1][j + 1] = color.getBlue();
            }
        }

        int r = clamp(bicubicFunction(reds, relativeX, relativeY), 0, 255);
        int g = clamp(bicubicFunction(greens, relativeX, relativeY), 0, 255);
        int b = clamp(bicubicFunction(blues, relativeX, relativeY), 0, 255);
        return new Color(r, g, b);
    }

    private int bicubicFunction(int[][] channels, double x, double y) {
        double b1 = (double) 1 / 4 * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2) * (y + 1);
        double b2 = (double) -1 / 4 * x * (x + 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1);
        double b3 = (double) -1 / 4 * y * (x - 1) * (x - 2) * (x + 1) * (y - 2) * (y + 1);
        double b4 = (double) 1 / 4 * x * y * (x - 2) * (x + 1) * (y - 2) * (y + 1);
        double b5 = (double) -1 / 12 * x * (x - 1) * (x - 2) * (y - 1) * (y - 2) * (y + 1);
        double b6 = (double) -1 / 12 * y * (x - 1) * (x - 2) * (x + 1) * (y - 1) * (y - 2);
        double b7 = (double) 1 / 12 * x * y * (x - 1) * (x - 2) * (y - 2) * (y + 1);
        double b8 = (double) 1 / 12 * x * y * (x - 2) * (x + 1) * (y - 1) * (y - 2);
        double b9 = (double) 1 / 12 * x * (x - 1) * (x + 1) * (y - 1) * (y - 2) * (y + 1);
        double b10 = (double) 1 / 12 * y * (x - 1) * (x + 1) * (x - 2) * (y - 1) * (y + 1);
        double b11 = (double) 1 / 36 * x * y * (x - 1) * (x - 2) * (y - 1) * (y - 2);
        double b12 = (double) -1 / 12 * x * y * (x - 1) * (x + 1) * (y - 2) * (y + 1);
        double b13 = (double) -1 / 12 * x * y * (x + 1) * (x - 2) * (y - 1) * (y + 1);
        double b14 = (double) -1 / 36 * x * y * (x - 1) * (x + 1) * (y - 1) * (y - 2);
        double b15 = (double) -1 / 36 * x * y * (x - 1) * (x - 2) * (y - 1) * (y + 1);
        double b16 = (double) 1 / 36 * x * y * (x - 1) * (x + 1) * (y - 1) * (y + 1);
        return (int) (b1 * channels[1][1] + b2 * channels[1][2] + b3 * channels[2][1] + b4 * channels[2][2] +
                        b5 * channels[1][0] + b6 * channels[0][1] + b7 * channels[2][0] + b8 * channels[0][2] +
                        b9 * channels[1][3] + b10 * channels[3][1] + b11 * channels[0][0] + b12 * channels[2][3] +
                        b13 * channels[3][2] + b14 * channels[0][3] + b15 * channels[3][0] + b16 * channels[3][3]);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return copy;
    }
}