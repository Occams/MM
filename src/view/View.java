package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Event.Result;
import model.algorithms.CopyMoveRobustMatch;
import model.algorithms.ICopyMoveDetection;

public class View extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ViewPanel panel;
	private JMenuBar menubar;
	private JFileChooser chooser;
	private ICopyMoveDetection algo = new CopyMoveRobustMatch();
	private JCheckBoxMenuItem multithreading, debugSwitch;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new View();
			}
		});
	}

	public View() {
		super();
		algo.addObserver(this);
		setVisible(true);
		setTitle("Copy-Move Robust Match Algorithm");
		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();
	}

	private void init() {
		panel = new ViewPanel();
		getContentPane().add(panel);
		initMBar();
		setJMenuBar(menubar);
	}

	private void log(String m) {
		panel.log.append("> " + m + "\n");
	}

	private void initMBar() {
		ImageIcon exitI = new ImageIcon("icons/exit.png");
		ImageIcon multithreadingI = new ImageIcon("icons/settings.png");
		ImageIcon debugI = new ImageIcon("icons/tool.png");
		ImageIcon openI = new ImageIcon("icons/open.png");
		menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		JMenu settings = new JMenu("Settings");
		file.setMnemonic(KeyEvent.VK_S);
		chooser = new JFileChooser(new File("."));
		chooser.setFileFilter(new FileNameExtensionFilter(
				"JPEG, GIF, BMP, PNG", "jpg", "jpeg", "gif", "bmp", "png"));
		JMenuItem exit = new JMenuItem("Exit", exitI);
		exit.setMnemonic(KeyEvent.VK_C);
		exit.setToolTipText("Exit application");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JMenuItem open = new JMenuItem("Open Image", openI);
		open.setMnemonic(KeyEvent.VK_O);
		open.setToolTipText("Open an image file");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = chooser.showOpenDialog(View.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					loadImage(chooser.getSelectedFile());
				}
			}
		});

		multithreading = new JCheckBoxMenuItem("Multithreading",
				multithreadingI);
		multithreading.setState(true);
		multithreading.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String action = multithreading.getState() ? "Enabled"
						: "Disabled";
				log(action + " multithreading");
			}
		});

		debugSwitch = new JCheckBoxMenuItem("Show Debugwindow",debugI);
		debugSwitch.setState(true);
		debugSwitch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panel.scrollP.setVisible(debugSwitch.getState());
				repaint();
				pack();
			}
		});

		file.add(open);
		file.addSeparator();
		file.add(exit);
		settings.add(multithreading);
		settings.add(debugSwitch);
		menubar.add(file);
		menubar.add(settings);
		log("Application started");
	}

	private void loadImage(File file) {
		try {
			BufferedImage i = ImageIO.read(file);
			log("Successfully loaded " + file.getName());
			panel.getImgPanel().setImage(i);
			panel.getStart().setEnabled(true);
		} catch (IOException e) {
			log("Error: Could not load image file\n");
		}
	}

	@Override
	public void update(Observable arg0, Object o) {
		if (o instanceof Result) {

		}
	}

	public class ViewPanel extends JPanel {

		private JPanel buttonPanel;
		private ImagePanel imagePanel;
		private JButton start, abort;
		private JLabel qualityL, thresholdL; 
		private JSlider quality, threshold;
		private JScrollPane scrollP;
		private JTextArea log;
		private ICopyMoveDetection algo;
		private static final long serialVersionUID = 1L;

		public ViewPanel() {
			super();
			init();
		}

		private void init() {
			setBackground(Color.GRAY);
			setLayout(new BorderLayout());
			initButtonPanel();
			log = new JTextArea();
			log.setEditable(false);
			log.setRows(5);
			log.setMargin(new Insets(5, 10, 5, 10));
			scrollP = new JScrollPane(log);
			scrollP.setAutoscrolls(true);
			imagePanel = new ImagePanel();
			add(scrollP, BorderLayout.NORTH);
			add(imagePanel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);

		}

		public ImagePanel getImgPanel() {
			return imagePanel;
		}

		public void setImgPanel(ImagePanel imagePanel) {
			this.imagePanel = imagePanel;
		}

		public JTextArea getLog() {
			return log;
		}

		public void setLog(JTextArea log) {
			this.log = log;
		}

		private void initButtonPanel() {
			ImageIcon abortI = new ImageIcon("icons/abort.png");
			ImageIcon startI = new ImageIcon("icons/start.png");
			GridLayout gLayout = new GridLayout(1, 6);
			gLayout.setVgap(20);
			gLayout.setHgap(0);
			buttonPanel = new JPanel(gLayout);
			start = new JButton("Start",startI);
			start.setEnabled(false);
			start.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					abort.setEnabled(true);
					start.setEnabled(false);
				}
			});
			abort = new JButton("Abort",abortI);
			abort.setEnabled(false);
			abort.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					algo.abort();
					abort.setEnabled(false);
				}
			});
			quality = new JSlider(0,100);
			quality.setToolTipText("Quality setting used to compute DCT coefficients");
			quality.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					float val = (float) quality.getValue() / 100.0f;
					qualityL.setText("Quality ["+val+"]:");
				}
			});
			threshold = new JSlider(1,20);
			threshold.setToolTipText("Threshold setting used by the algorithm");
			threshold.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					thresholdL.setText("Threshold ["+threshold.getValue()+"]: ");
				}
			});
			qualityL = new JLabel("Quality [0.5]:");
			qualityL.setHorizontalAlignment(JLabel.CENTER);
			thresholdL = new JLabel("Threshold [10]:");
			thresholdL.setHorizontalAlignment(JLabel.CENTER);
			buttonPanel.add(start);
			buttonPanel.add(abort);
			buttonPanel.add(qualityL);
			buttonPanel.add(quality);
			buttonPanel.add(thresholdL);
			buttonPanel.add(threshold);
		}

		public JButton getStart() {
			return start;
		}

		public void setStart(JButton start) {
			this.start = start;
		}

		public JButton getAbort() {
			return abort;
		}

		public void setAbort(JButton abort) {
			this.abort = abort;
		}

		public class ImagePanel extends JPanel {
			private static final long serialVersionUID = 1L;
			private BufferedImage image = null;

			public ImagePanel() {
				super();
				setBackground(Color.BLACK);
				setVisible(true);
			}

			public void paint(Graphics g) {
				g.setColor(Color.DARK_GRAY);
				g.fillRect(0, 0, getWidth(), getHeight());

				if (image != null) {
					double panelRatio = (double) getWidth()
							/ (double) getHeight();
					double aspectRatio = (double) image.getWidth()
							/ (double) image.getHeight();
					int nWidth = getWidth(), nHeight = getHeight();

					if (panelRatio < aspectRatio) {
						nHeight = (int) (nWidth / aspectRatio);
					} else {
						nWidth = (int) (nHeight * aspectRatio);
					}
					g.drawImage(image, (getWidth() - nWidth) / 2,
							(getHeight() - nHeight) / 2, nWidth, nHeight, null);
				}
			}

			public void setImage(BufferedImage image) {
				this.image = image;
				repaint();
			}

			public BufferedImage getImage() {
				return image;
			}

		}

	}
}
