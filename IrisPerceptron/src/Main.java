
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main {
static String klasa1;
static String klasa0;
static String klasa2="versicolor";
static Vector vectorWag;
static float alpha;
static int k;
static float teta=0;




	public static void main(String[] args) {
		int columns=countColumns("iristrain.csv");
		int lines=countLines("iristrain.csv");
		
		JFrame frame=new JFrame();
		JPanel panel=new JPanel();
		BoxLayout bl=new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
			JPanel classPanel=new JPanel();
				JLabel kl=new JLabel("k: ");
				classPanel.add(kl);
				JTextField kValue=new JTextField("100");
				classPanel.add(kValue);
				JLabel a=new JLabel("Alpha: ");
				classPanel.add(a);
				JTextField aValue=new JTextField("0.5");
				classPanel.add(aValue);
				JLabel class1=new JLabel("Class 1: ");
				classPanel.add(class1);
				JTextField class1Value=new JTextField("setosa");
				class1Value.setEditable(false);
				classPanel.add(class1Value);
				JLabel class0=new JLabel("Class 0: ");
				classPanel.add(class0);
				JTextField class0Value=new JTextField("virginica");
				class0Value.setEditable(false);
				classPanel.add(class0Value);
			panel.add(classPanel);
			JPanel vectorPanel=new JPanel();
				JLabel[] labels=new JLabel[columns];
				JTextField[] textFields=new JTextField[columns];
				for(int i=0;i<textFields.length;i++) {
					labels[i]=new JLabel("x"+(i+1)+": ");
					textFields[i]=new JTextField();
					textFields[i].setColumns(2);
					vectorPanel.add(labels[i]);
					vectorPanel.add(textFields[i]);
				}
				JButton calc=new JButton("Calculate");
				calc.setBackground(SystemColor.controlHighlight);
				vectorPanel.add(calc);
			panel.add(vectorPanel);
			JPanel resultPanel=new JPanel();
				JLabel result=new JLabel("Result: ");
				resultPanel.add(result);
				JTextField resultValue=new JTextField("N/A");
				resultValue.setEditable(false);
				resultPanel.add(resultValue);
			panel.add(resultPanel);
		frame.add(panel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        
        calc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				klasa1=class1Value.getText();
				klasa0=class0Value.getText();
				alpha=Float.parseFloat(aValue.getText());
				k=Integer.parseInt(kValue.getText());
				vectorWag=new Vector(new float[textFields.length]);
				
				Vector[] trainVectors=new Vector[lines];
				fill(trainVectors, "iristrain.csv");
				
				for(int i=0;i<k;i++)
					train(trainVectors);
				
				boolean empty=false;
				for(int i=0;i<textFields.length;i++) {
					if(textFields[i].getText().equals("")==true) {
						empty=true;
						break;
					}
				}
				
				if(empty==false) {
					float[] tmp=new float[textFields.length];
					for(int i=0;i<tmp.length;i++) {
						tmp[i]=Float.parseFloat(textFields[i].getText());
					}
					Vector vector=new Vector(tmp);
					resultValue.setText(
							perceptron(vector)
					);
					for(int i=0;i<textFields.length;i++) {
						textFields[i].setText("");
					}
					frame.pack();
				} else {
					Vector[] testVectors=new Vector[countLines("iristest.csv")];
					fill(testVectors, "iristest.csv");
					
					int correct=0;
					for(int i=0;i<testVectors.length;i++) {
						if(testVectors[i].species.equals(perceptron(testVectors[i]))) {
							correct++;
						}
					}
					int tmp=correct*100/testVectors.length;
					resultValue.setText(correct+" "+tmp+"%");
					frame.pack();
				}
				
			}
        });
	}

	public static int countLines(String file) {
		int toReturn=0;
		Scanner sc;
		try {
			sc=new Scanner(new File(file));
			sc.nextLine();
			while(sc.hasNextLine()) {
				if(sc.nextLine().contains(klasa2)==false)
					toReturn++;
			}
		} catch (FileNotFoundException e) {}
		return toReturn;
	}

	public static int countColumns(String file) {
		int toReturn=0;
		Pattern p=Pattern.compile(",");
		Matcher m;
		Scanner sc;
		try {
			sc=new Scanner(new File(file));
			sc.nextLine();
			m=p.matcher(sc.nextLine());
			while(m.find())
				toReturn++;
		} catch (FileNotFoundException e) {}
		return toReturn-1;
		
	}

	public static void fill(Vector[] vectors, String file) {
		Scanner sc;
		try {
			sc=new Scanner(new File(file));
			sc.nextLine();
			for(int i=0;sc.hasNextLine();) {
				String[] tmp=sc.nextLine().split(",");
				if(tmp[tmp.length-1].contains(klasa2)==false) {
					float[] tmp1=new float[countColumns(file)];
					for(int j=0;j<tmp1.length;j++) {
						tmp1[j]=Float.parseFloat(tmp[j+1]);
					}
					String tmp2=tmp[tmp.length-1].replaceAll("\"", "");
					vectors[i]=new Vector(tmp1, tmp2);
					i++;
				}
			} sc.close();
		} catch (NumberFormatException e) {} catch (FileNotFoundException e) {}
	}

	public static String perceptron(Vector vector) {
		float suma=0;
		for(int j=0;j<vector.measures.length;j++) {
			suma+=vector.measures[j]*vectorWag.measures[j];
		}

		if(suma>=teta)
			return klasa1;
		else
			return klasa0;
	}
	
	public static void train(Vector[] trainVectors) {
		for(int i=0;i<trainVectors.length;i++) {
			
			String wynik=perceptron(trainVectors[i]);
			
			if(wynik.equals(trainVectors[i].species)==false) {
				for(int j=0;j<vectorWag.measures.length;j++) {
					if(wynik.equals(klasa1)) {
						vectorWag.measures[j]+=trainVectors[i].measures[j]*-1*alpha;
						teta=teta*1*alpha;
					} else {
						vectorWag.measures[j]+=trainVectors[i].measures[j]*1*alpha;
						teta=teta*-1*alpha;
					}
				}
			}	
		}
	}
}
