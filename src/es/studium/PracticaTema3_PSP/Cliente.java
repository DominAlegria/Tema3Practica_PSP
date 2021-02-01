package es.studium.PracticaTema3_PSP;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream; 
import java.io.DataOutputStream; 
import java.io.IOException; 
import java.net.Socket; 
import javax.swing.JButton;
import javax.swing.JFrame; 
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea; 
import javax.swing.JTextField;

public class Cliente extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 3876973990022961575L;
	Socket socket;
	DataInputStream fEntrada;
	DataOutputStream fSalida;
	String nombreJugador;
	static JTextField mensaje = new JTextField(); 
	private JScrollPane scrollpane; 
	static JTextArea textarea;
	JButton boton = new JButton("Enviar"); 
	JButton desconectar = new JButton("Salir"); 
	boolean repetir = true;
	
	public Cliente(Socket socket, String nombreJugador)
	{ 
		// Prepara la pantalla. Se recibe el socket creado y el nombre del cliente 
		super(" Conexión del Jugador: " + nombreJugador); 
		setLayout(null);
		mensaje.setBounds(10, 10, 400, 30);
		add(mensaje);
		textarea = new JTextArea();
		scrollpane = new JScrollPane(textarea);
		scrollpane.setBounds(10, 50, 400, 300); 
		add(scrollpane);
		boton.setBounds(420, 10, 100, 30);
		add(boton); 
		desconectar.setBounds(420, 50, 100, 30);
		add(desconectar); 
		textarea.setEditable(false);
		boton.addActionListener(this); 
		this.getRootPane().setDefaultButton(boton);
		desconectar.addActionListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.socket = socket; 
		this.nombreJugador = nombreJugador;
		
		/* Se crean los flujos de entrada y salida.
		   En el flujo de salida se escribe un mensaje 
		   indicando que el Jugador se ha unido al Juego.
		   El HiloServidor recibe este mensaje y
	       lo reenvía a todos los Jugadores que estan participando */
		try
		{
			fEntrada = new DataInputStream(socket.getInputStream());
			fSalida = new DataOutputStream(socket.getOutputStream());
			String texto = "> " + nombreJugador + " entra en el juego.";
			fSalida.writeUTF(texto);
		}
		catch (IOException e)
		{
			System.out.println("Error de E/S");
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/* El método main es el que lanza el Jugador, 
	   para ello en primer lugar se solicita el nombre o nick del
	   Jugador, una vez especificado el nombre 
	   se crea la conexión al servidor y se crear la pantalla del Juego(jugador)
	   lanzando su ejecución (ejecutar()).*/
	
	public static void main(String[] args) throws Exception 
	{
		int puerto = 44444; 
		String nombre = JOptionPane.showInputDialog("Introduce tu nombre o nick:"); 
		Socket socket = null;
		try 
		{
			socket = new Socket("127.0.0.1", puerto);
		}
		catch (IOException ex)
		{ 
			ex.printStackTrace(); 
			JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:1>>", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} 
		if(!nombre.trim().equals("")) 
		{
			Cliente cliente = new Cliente(socket, nombre);
			cliente.setBounds(0,0,540,400); 
			cliente.setVisible(true);
			cliente.ejecutar();
		} 
		else
		{ 
			System.out.println("El nombre está vacío...");
		}
	}
	
	/* Cuando se pulsa el botón Enviar, 
	   el mensaje introducido se envía al servidor por el flujo de salida*/
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource() == boton)
		{ 
			String texto = nombreJugador + "> " + mensaje.getText();
			try
			{
				mensaje.setText(""); 
				fSalida.writeUTF(texto);
			} 
			catch (IOException ex)
			{ 
				ex.printStackTrace();
			}
		}
		
		/* Si se pulsa el botón Salir, 
		   se envía un mensaje indicando que el Jugador abandona el chat
		   y también se envía un * para indicar 
		   al servidor que el Jugador se ha cerrado */
		else if(arg0.getSource() == desconectar)
		{ 
			String texto = "SERVIDOR> Abandona el chat... " + nombreJugador;
			try 
			{ 
				fSalida.writeUTF(texto);
				fSalida.writeUTF("*"); 
				repetir = false;
			} 
			catch (IOException ex) 
			{ 
				ex.printStackTrace();
			} 
		} 
	}
	
	/* Dentro del método ejecutar(), el Jugador lee lo que el
	   hilo le manda (mensajes del Chat) y lo muestra en el textarea.
	   Esto se ejecuta en un bucle del que solo se sale
	   en el momento que el Jugador pulse el botón Salir
	   y se modifique la variable repetir*/	
	public void ejecutar() 
	{ 
		String texto = "";
		while(repetir) 
		{ 
			try
			{ 
				texto = fEntrada.readUTF();
				textarea.setText(texto);
			}
			catch (IOException ex) 
			{ 
				JOptionPane.showMessageDialog(null, "Imposible conectar con el servidor \n" + ex.getMessage(), "<<Mensaje de Error:2>>", JOptionPane.ERROR_MESSAGE);
				repetir = false;
			}
		}
		try
		{ 
			socket.close(); 
			System.exit(0);
		} 
		catch (IOException ex)
		{ 
			ex.printStackTrace();
		}
	} 
	
}
