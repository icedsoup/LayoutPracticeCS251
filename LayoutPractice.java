import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.*;
import java.awt.geom.Point2D;

/*
 * Brendan Mecheski
 * CS251
 * LayoutPractice.java
 * A simple gui
 * 
 * NOTE: you may need to run the jar via cmd for it to open if you're getting a Java exception error when double clicking
 * no idea why, maybe a version mismatch
 * 
 */

public class LayoutPractice extends JPanel {

    //vars
    private static int meowCount = 0;
    private static String meows = "";
    private double angleOffset = 0;
    private static double rotateSpeed = 0.1;

    //main
    public static void main(String[] args) {

        //window
        JFrame frame = new JFrame("meower");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //icon
        Image icon = new ImageIcon(LayoutPractice.class.getResource("/assets/icon.png")).getImage();
        frame.setIconImage(icon);
        

        //meow count label
        JLabel meowCountLabel = new JLabel(meowCount + " meows");
        meowCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        meowCountLabel.setVerticalAlignment(SwingConstants.CENTER);

        //volume slider
        JSlider volumeSlider = new JSlider(-82, 0, -41);
        volumeSlider.addChangeListener(e -> {
            float volume = volumeSlider.getValue();
            System.out.println(volume);
        });
        JLabel volumeLabel = new JLabel("volume");
        volumeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        volumeLabel.setVerticalAlignment(SwingConstants.CENTER);


        //dialog window
        JDialog dialog = new JDialog(frame, "meow", false);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());
        JLabel dialogLabel = new JLabel(meows, SwingConstants.CENTER);
        dialog.add(dialogLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("OK");
        closeButton.addActionListener(e -> dialog.dispose());
        closeButton.addActionListener(e -> meows = "");
        dialog.add(closeButton, BorderLayout.SOUTH);
        dialog.pack();

        //:3 button
        JButton sillyButton = new JButton(":3");
        sillyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                playSound("assets/meow.wav", volumeSlider.getValue());

                meowCount++;
                meows += ":3 ";

                meowCountLabel.setText(meowCount + " meows");
                dialogLabel.setText(meows);
                dialog.pack();

                dialog.setVisible(true);
                

            }
        });
        sillyButton.setPreferredSize(new Dimension(60, 50));


        //rapid fire for :3 button
        Timer rapidFire = new Timer(100, e -> {
            sillyButton.doClick();
        });

        sillyButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                rapidFire.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                rapidFire.stop();
            }
        });

        //title
        JLabel titleLabel = new JLabel("meower");

        //cat
        ImageIcon tudou = new ImageIcon(LayoutPractice.class.getResource("/assets/cat.gif"));
        JLabel cat = new JLabel(tudou);
        cat.setHorizontalAlignment(SwingConstants.CENTER);
        cat.setVerticalAlignment(SwingConstants.CENTER);

        //rotation button
        JButton rotation = new JButton("spin");
        rotation.addActionListener(e -> rotateSpeed++);
        rotation.setPreferredSize(new Dimension(60, 50));

        //layout

        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);

        JPanel main = new JPanel(new BorderLayout());

        JPanel main2 = new JPanel(new FlowLayout());
        main2.add(sillyButton, FlowLayout.LEFT);
        main2.add(rotation, FlowLayout.LEFT);
        main2.setBackground(Color.WHITE);

        main.add(main2, BorderLayout.SOUTH);
        main.add(meowCountLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new OverlayLayout(centerPanel));
        centerPanel.setOpaque(false); 
        cat.setAlignmentX(0.5f); 
        cat.setAlignmentY(0.5f); 
        LayoutPractice sunPanel = new LayoutPractice();
        sunPanel.setAlignmentX(0.5f);
        sunPanel.setAlignmentY(0.5f);
        sunPanel.setOpaque(false);
        centerPanel.add(cat);
        centerPanel.add(sunPanel);
        main.add(centerPanel, BorderLayout.CENTER);

        main.setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new FlowLayout());
        topBar.add(titleLabel, FlowLayout.LEFT);
        topBar.setBackground(Color.WHITE);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(volumeSlider, BorderLayout.SOUTH);
        bottomBar.add(volumeLabel, BorderLayout.CENTER);
        bottomBar.setBackground(Color.WHITE);

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(bottomBar, BorderLayout.SOUTH);
        frame.add(main, BorderLayout.CENTER);

        frame.setVisible(true);

    }

    //shape drawer
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D sun = (Graphics2D) g;

        sun.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = 150;

        RadialGradientPaint gradient = new RadialGradientPaint(
            new Point2D.Float(cx, cy),
            radius+100,
            new float[] {0f, 1f},
            new Color[] {new Color(255, 255, 0), new Color(255, 255, 255)}
        );
        sun.setPaint(gradient);
        sun.fillOval(cx-radius, cy-radius, radius*2, radius*2);

        sun.setStroke(new BasicStroke(15));
        sun.setPaint(gradient);

        int rayLength = 500;
        int numRays = 30;
        for (int i = 0; i < numRays; i++) {

            double angle = angleOffset + 2 * Math.PI * i / numRays;
            int x1 = (int) (cx + Math.cos(angle) * radius);
            int y1 = (int) (cy + Math.sin(angle) * radius);
            int x2 = (int) (cx + Math.cos(angle) * rayLength);
            int y2 = (int) (cy + Math.sin(angle) * rayLength);
            sun.drawLine(x1, y1, x2, y2);

        }

        setBackground(Color.WHITE);

    }

    //shape animation
    public LayoutPractice() {

    Timer animation = new Timer(16, e -> {
        angleOffset += Math.toRadians(rotateSpeed);
        repaint();
    });
    animation.start();

    }

    //the sound player
    public static void playSound(String resourcePath, float volume) {

          try (InputStream audioSrc = LayoutPractice.class.getResourceAsStream(resourcePath)) {

            if (audioSrc == null) {

                System.err.println("Could not find sound: " + resourcePath);
                return;

            }

            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume);

            clip.start();

        }

        catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {

            e.printStackTrace();

        }

    }

}