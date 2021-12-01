package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ServletInsertar")
public class ServletInsertar extends HttpServlet {
	
	private String jdbcUrl = "jdbc:h2:file:~/testdb";


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String nombrePersona = req.getParameter("nombrePersona");
		String nombrePerro = req.getParameter("nombrePerro");
		
		
		
		Connection conn;
		try {
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection(jdbcUrl , "sa", "");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		PreparedStatement preparedStatement = null;
		try {
			
			preparedStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS PERROS (nombrePersona VARCHAR(100), nombrePerro VARCHAR(100))");
			preparedStatement.executeUpdate();
			preparedStatement.close();
			preparedStatement = conn.prepareStatement("INSERT INTO PERROS(nombrePersona, nombrePerro) VALUES (?, ?)");
			preparedStatement.setString(1, nombrePersona);
			preparedStatement.setString(2, nombrePerro);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			System.out.println("IMPRIMIENDO LISTADO");
			preparedStatement = conn.prepareStatement("SELECT nombrePersona, nombrePerro FROM PERROS");
			
			ResultSet resultSet = preparedStatement.executeQuery();
			
			ArrayList<String> listNombres = new ArrayList<String>();
			ArrayList<String> listPerros = new ArrayList<String>();
			
			while (resultSet.next()) {
				String nombres = resultSet.getString(1);
				listNombres.add(nombres);
				String perros = resultSet.getString(2); 
				listPerros.add(perros);
				System.out.println(nombres + "-" + perros);
				
			} 
			req.setAttribute("nombrePersona", listNombres);
			req.setAttribute("nombrePerro", listPerros);
			preparedStatement.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/fin.jsp");
		dispatcher.forward(req, res); 

	}

}
