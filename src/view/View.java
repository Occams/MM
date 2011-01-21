package view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class View extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ViewPanel panel;
	private JMenuBar menubar;
	private JFileChooser chooser;
	private File image;

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
		chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("JPEG, GIF, BMP",
				"jpg", "jpeg", "gif", "bmp"));
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
					image = chooser.getSelectedFile();
					loadImage();
				}
			}
		});

		file.add(open);
		file.addSeparator();
		file.add(exit);
		menubar.add(file);
		panel.getLog().append("Application started\n");
	}
	
	private void loadImage() {
		try {
			BufferedImage i = ImageIO.read(image);
			panel.getLog().append("Successfully loaded "+image.getName()+"\n");
			panel.getImgPanel().setImage(i);
			panel.getStart().setEnabled(true);
		} catch (IOException e) {
			panel.getLog().append("Error: Could not load image file\n");
		}
	}
	

	@Override
	public void update(Observable arg0, Object arg1) {

	}
}
