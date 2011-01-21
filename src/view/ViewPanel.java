package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.ReplicateScaleFilter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

public class ViewPanel extends JPanel {

	private JPanel buttonPanel;
	private ImagePanel imagePanel;
	private JButton start, abort;
	private JSpinner quality, threshold;
	private JTextArea log;
	
	/**
	 * 
	 */
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
		log.setAutoscrolls(true);
		log.setEditable(false);
		log.setRows(5);
		log.setMargin(new Insets(5, 10, 5, 10));
		imagePanel = new ImagePanel();
		add(log,BorderLayout.NORTH);
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
		abort = new JButton("Abort");
		abort.setEnabled(false);
		quality = new JSpinner(new SpinnerNumberModel(0.5f, 0f, 1f, 0.05f));
		quality.setToolTipText("Quality setting used to compute DCT coefficients");
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
		private Image image = null;
		
		public ImagePanel() {
			super();
		}
		
		public void paint(Graphics g) {
			if(image != null) {
				image = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_FAST);
				g.drawImage(image,0,0,null);
			}
		}

		public void setImage(Image image) {
			this.image = image;
			repaint();
		}

		public Image getImage() {
			return image;
		}
		
	}

}
