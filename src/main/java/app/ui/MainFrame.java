package app.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JList;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

	JPanel imagePanel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(640, 480));
		setMinimumSize(new Dimension(640, 480));
		setTitle("Duplicate Cleaner");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		imagePanel  = new JPanel();
		imagePanel.setBackground(Color.WHITE);
		getContentPane().add(imagePanel, BorderLayout.CENTER);
	
	}
	
	public void clearImagePanel() {
		imagePanel.removeAll();
		imagePanel.revalidate();
	}
	
	public void displayDuplicates(String[] dups) {
		if (dups.length < 2) {
			return;
		}
		for (String filename : dups) {
			BufferedImage myPicture = null;
			try {
				myPicture = ImageIO.read(new File(filename));
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			JLabel filenameLabel = new JLabel(filename);
			JLabel picLabel = new JLabel(getScaledImage(myPicture));
			imagePanel.add(filenameLabel, BorderLayout.NORTH);
			imagePanel.add(picLabel, BorderLayout.CENTER);
			imagePanel.revalidate();
		}
	}
	
	private ImageIcon getScaledImage(BufferedImage img) {
		double ratio = (double) img.getWidth() / img.getHeight();
		int newWidth = imagePanel.getWidth() / 5;
		int newHeight = (int) Math.floor(newWidth / ratio);
		return new ImageIcon(img.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_FAST));
	}

}
