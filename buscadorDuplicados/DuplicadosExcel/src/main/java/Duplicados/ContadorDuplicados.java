package Duplicados;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContadorDuplicados {

	// Clase Contacto actualizada
    public static class Contacto {
        String contactID, name, lastName, email, postalZip, address;

        // Constructor para inicializar un contacto con sus datos
        public Contacto(String contactID, String name, String lastName, String email, String postalZip, String address) {
            this.contactID = contactID;
            this.name = name;
            this.lastName = lastName;
            this.email = email;
            this.postalZip = postalZip;
            this.address = address;
        }
    }

 // Mwtodo para leer archivo Excel y extraer datos
    public static List<Contacto> leerExcel(File file) throws IOException {
        List<Contacto> contacto = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file); // Abrir el archivo
        	 
        	// Crear el workbook para manejar el archivo Excel
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); //primera hoja del Excel
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Saltar encabezado
                String contactoID = getValorCeldaString(row.getCell(0)); 	// Obtener el ID del contacto
                String nombre = getValorCeldaString(row.getCell(1));		// Obtener el nombre
                String apellido = getValorCeldaString(row.getCell(2));		// Obtener el apellido
                String email = getValorCeldaString(row.getCell(3));			// Obtener el correo electronico
                String postalZip = getValorCeldaString(row.getCell(4));		// Obtener el código postal
                String direccion = getValorCeldaString(row.getCell(5));		// Obtener la dirección
                contacto.add(new Contacto(contactoID, nombre, apellido, email, postalZip, direccion)); // Crear y agregar la dila a la lista de contacto
            }
        }
        return contacto; // Retornar la lista 
    }

 // Metodo para obtener el valor de una celda
    private static String getValorCeldaString(Cell cell) {
        if (cell == null) return ""; 											// Retornar vacio si la celda es nula
        return switch (cell.getCellType()) {									// Identificar el tipo de celda
            case STRING -> cell.getStringCellValue();							// Retornar el valor si es texto
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());	// Convertir valores numericos a texto
            default -> "";														// Retornar vacio para otros tipos de celdas
        };
    }

 // Metodo para calcular el nivel de precision
    private static String calculaPrecision(Contacto c1, Contacto c2) {
        int contador = 0;													// Contador de coincidencias
        if (c1.name.equalsIgnoreCase(c2.name)) contador++;					// Comparar nombres
        if (c1.lastName.equalsIgnoreCase(c2.lastName)) contador++;			// Comparar apellidos
        if (c1.email.equalsIgnoreCase(c2.email)) contador++;				// Comparar correos electronicos
        if (c1.postalZip.equals(c2.postalZip)) contador++;					// Comparar códigos postales
        if (c1.address.equalsIgnoreCase(c2.address)) contador++;			// Comparar direcciones

        // Determinar precisión basada en la cantidad de coincidencias
        return switch (contador) {
            case 4, 5 -> "Alta";			// Alta precision si hay 4 o más coincidencias
            case 2, 3 -> "Media";			// precision media si hay 2 o 3 coincidencias
            case 1 ->  "Baja";				// Precisión baja si hay 1 coincidencia
            default -> "sin Coincidencia";	// Predeterminado es si no hay
        };
    }

 // Metodo para encontrar contactos duplicados 
    public static List<String[]> buscaDuplicados(List<Contacto> contacts) {
        List<String[]> duplicados = new ArrayList<>();							// Lista para almacenar los duplicados
        for (int i = 0; i < contacts.size(); i++) {								// Comparar cada contacto con los demas
            for (int j = i + 1; j < contacts.size(); j++) {
            	Contacto c1 = contacts.get(i);
            	Contacto c2 = contacts.get(j);
                String precision = calculaPrecision(c1, c2);					// Calcular precision entre dos contactos
                if (!precision.equals("sin Coincidencia")) { 					// Solo incluir coincidencias
                	duplicados.add(new String[]{c1.contactID, c2.contactID, precision});	// Agregar duplicado a la lista
                }
            }
        }
        return duplicados;	// Retornar la lista de duplicados
    }

 // Método principal e Interfaz Grafica
    public static void main(String[] args) {
        JFrame frame = new JFrame("Identificador de Duplicados");		// Crear la ventana principal y lo nombra
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			// Configurar cierre de la ventana
        frame.setSize(600, 400);										// Establecer el tamaño de la ventana

        JButton uploadButton = new JButton("Seleccione Archivo Excel");	// Boton para cargar archivo Excel
        JTable resultTable = new JTable();								// Tabla para mostrar resultados
        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ContactoID Origen", "ContactoID Coincidencia", "Precisión"}, 0); // modelo que mostrara la tabla
        resultTable.setModel(tableModel);	// Asignar el modelo a la tabla
        JScrollPane scrollPane = new JScrollPane(resultTable);	// Agregar barra de desplazamiento a la tabla

        // Al presionar el boton de carga ejecuta este codigo
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();					// Crear un selector de archivos
            int returnValue = fileChooser.showOpenDialog(null);				// Mostrar el cuadro de dialogo
            
            // Si se selecciona un archivo
            if (returnValue == JFileChooser.APPROVE_OPTION) {				
                File selectedFile = fileChooser.getSelectedFile();			// Obtener el archivo seleccionado
                try {
                    List<Contacto> contacto = leerExcel(selectedFile);		// Leer contactos desde el archivo
                    List<String[]> duplicates = buscaDuplicados(contacto);	// Encontrar duplicados

                    // Limpiar tabla
                    tableModel.setRowCount(0);

                    // Agregar filas con duplicados
                    for (String[] duplicate : duplicates) {
                        tableModel.addRow(duplicate);
                    }
                    
                    // Mostrar mensaje si no se encontraron duplicados
                    if (duplicates.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "No se encontraron duplicados.");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error leyendo el archivo: " + ex.getMessage()); // Mostrar mensaje de error
                }
            }
        });

     // Por ultimo agregar componentes a la ventana
	    frame.getContentPane().add(uploadButton, "North");	// Boton en la parte superior
	    frame.getContentPane().add(scrollPane, "Center");	// Tabla en el centro
	    frame.setVisible(true);								// Mostrar la ventana					
	}
}