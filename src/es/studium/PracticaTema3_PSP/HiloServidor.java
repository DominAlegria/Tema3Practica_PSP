package es.studium.PracticaTema3_PSP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



public class HiloServidor extends Thread
{
	DataInputStream fEntrada;
	Socket socket;
	
	public HiloServidor(Socket socket)
	{
		this.socket = socket;
		try 
		{
			fEntrada = new DataInputStream(socket.getInputStream());
		}
		catch (IOException e)
		{
			System.out.println("Error de E/S");
		}
	}
	
	/* En el método run() lo primeo que hacemos es enviar todos los mensajes
	   actuales al cliente que se acaba de incorporar*/
	public synchronized void run()
	{
		Servidor1.mensaje.setText("El número a adivinar es: " + Servidor1.random);
		Servidor1.mensaje1.setText(" " + Servidor1.ACTUALES);
		String texto = Servidor1.textarea.getText();
		EnviarMensajes(texto);
		
		/* Seguidamente, se crea un bucle en el que se recibe lo que el cliente escribe en el chat.
		   Cuando un cliente finaliza con el botón Salir, se envía un * al servidor del Chat,
		   entonces se sale del bucle while, ya que termina el proceso del cliente,
		   de esta manera se controlan las conexiones actuales */
		while (true)
		{
			String cadena = "";
			try
			{
				cadena = fEntrada.readUTF();
				if(cadena.trim().equals("*"))
				{
					Servidor1.ACTUALES--;
					Servidor1.mensaje.setText("El número a adivinar es: " + Servidor1.random);
					Servidor1.mensaje1.setText(" " + Servidor1.ACTUALES);

					break;
				}
				
				/* Comprueba si el número dado por el jugador es mayor, menor o igual que el número oculto*/
				else
				{
					if(cadena.contains("."))
					{
						Servidor1.textarea.append(cadena + "\n"); 
					}
					else
					{
						String[] arrayJuego = cadena.split("> ");
						String nombreJugador = arrayJuego[0];
						String numeroDadoJugador = arrayJuego[1];
						
						// Hacemos que hasta que no pasen 3 segundos no vuelva a pedir número a otra persona.
						Thread.sleep(3000);
						if (Integer.parseInt(numeroDadoJugador) < Servidor1.random)
						{
							Servidor1.textarea.append("> " + nombreJugador + " piensa que el número es el " + numeroDadoJugador + ", pero el número es mayor a " + numeroDadoJugador + ". \n");
						}
						else if (Integer.parseInt(numeroDadoJugador) > Servidor1.random)
						{
							Servidor1.textarea.append("> " + nombreJugador + " piensa que el número es el " + numeroDadoJugador + ", pero el número es menor a " + numeroDadoJugador + ". \n");
						}
						else if (Integer.parseInt(numeroDadoJugador) == Servidor1.random)
						{
							Servidor1.textarea.append("> " + nombreJugador + " piensa que el número es el " + numeroDadoJugador + ", y ha ACERTADOOOO!!!!. \n" + "Juego finalizado...");
							texto = Servidor1.textarea.getText();
							EnviarMensajes(texto);
						}
					
					}
				}
				texto = Servidor1.textarea.getText();
				EnviarMensajes(texto);
			}
			catch (Exception ex)
			{
				try
				{
					fEntrada.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				ex.printStackTrace();
				break;
			}
		}
	}
	
	/* El método EnviarMensajes() envía el texto del textarea a
	   todos los sockets que están en la tabla de sockets, 
	   de esta forma todos ven la conversación. 
	   El programa abre un stream de salida para escribir el texto en el socket*/
	private void EnviarMensajes(String texto)
	{ 
		for(int i=0; i<Servidor1.CONEXIONES; i++)
		{
			Socket socket = Servidor1.tabla[i];
			try 
			{ 
				DataOutputStream fSalida = new DataOutputStream(socket.getOutputStream());
				fSalida.writeUTF(texto);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			} 
		}
	}	
}