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
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
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
		setResizable(false);
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

	private void initMBar() {
		menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);
		chooser = new JFileChooser(new File("."));
		chooser.setFileFilter(new FileNameExtensionFilter("JPEG, GIF, BMP, PNG",
				"jpg", "jpeg", "gif", "bmp","png"));
		JMenuItem exit = new JMenuItem("Exit");
		exit.setMnemonic(KeyEvent.VK_C);
		exit.setToolTipText("Exit application");
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JMenuItem open = new JMenuItem("Open Image");
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

		file.add(open);
		file.addSeparator();
		file.add(exit);
		menubar.add(file);
		panel.getLog().append("Application started\n");
	}

	private void loadImage(File file) {
		try {
			BufferedImage i = ImageIO.read(file);
			panel.getLog().append(
					"Successfully loaded " + file.getName() + "\n");
			panel.getImgPanel().setImage(i);
			panel.getStart().setEnabled(true);
		} catch (IOException e) {
			panel.getLog().append("Error: Could not load image file\n");
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
		private JSpinner quality, threshold;
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
			JScrollPane scrollP = new JScrollPane(log);
			scrollP.setAutoscrolls(true);
			imagePanel = new ImagePanel();
			add(scrollP,BorderLayout.NORTH);
			add(imagePanel,BorderLayout.CENTER);
			add(buttonPanel,BorderLayout.SOUTH);

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

		public JSpinner getQuality() {
			return quality;
		}

		public void setQuality(JSpinner quality) {
			this.quality = quality;
		}

		public JSpinner getThreshold() {
			return threshold;
		}

		public void setThreshold(JSpinner threshold) {
			this.threshold = threshold;
		}

		private void initButtonPanel() {
			buttonPanel = new JPanel(new GridLayout(1, 6));
			start = new JButton("Start");
			start.setEnabled(false);
			start.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					abort.setEnabled(true);
					start.setEnabled(false);
					algo.detect(imagePanel.getImage(), (Float) quality
							.getValue(), (Integer) threshold.getValue(), null);

				}
			});
			abort = new JButton("Abort");
			abort.setEnabled(false);
			abort.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					algo.abort();
					abort.setEnabled(false);
				}
			});
			quality = new JSpinner(new SpinnerNumberModel(0.5f, 0f, 1f, 0.05f));
			quality
					.setToolTipText("Quality setting used to compute DCT coefficients");
			threshold = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
			quality.setToolTipText("Threshold setting used by the algorithm");
			JLabel qualityL = new JLabel("Quality [0..1]:");
			qualityL.setHorizontalAlignment(JLabel.CENTER);
			JLabel thresholdL = new JLabel("Threshold [1..20]:");
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
				
				if(image != null) {
					double panelRatio = (double) getWidth() / (double)getHeight();
					double aspectRatio = (double) image.getWidth() / (double) image.getHeight();
					int nWidth = getWidth(), nHeight = getHeight();
					
					if (panelRatio < aspectRatio) {
						nHeight = (int) (nWidth / aspectRatio);
					} else {
						nWidth = (int) (nHeight * aspectRatio);
					}
					g.drawImage(image,(getWidth()-nWidth) /2, (getHeight()-nHeight) /2,nWidth,nHeight,null);
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
