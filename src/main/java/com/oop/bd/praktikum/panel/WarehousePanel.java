package com.oop.bd.praktikum.panel;

import com.oop.bd.praktikum.controller.WarehouseController;
import com.oop.bd.praktikum.dto.WarehouseDTO;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class WarehousePanel {

    public JPanel createWarehousePanel(DefaultCellEditor editor, WarehouseController warehouseController) {
        // Create the main panel with a border layout
        JPanel warehousePanel = new JPanel(new BorderLayout());

        // Create the table model for the JTable
        DefaultTableModel warehouseTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Name"});

        // Create the JTable using the table model
        JTable warehouseTable = new JTable(warehouseTableModel);

        // Set the editor in order to forbid double click editing of the rows
        warehouseTable.setDefaultEditor(Object.class, editor);

        // Create a JScrollPane for the JTable and add it to the main panel
        JScrollPane warehouseTableScrollPane = new JScrollPane(warehouseTable);
        warehousePanel.add(warehouseTableScrollPane, BorderLayout.CENTER);

        // Create a panel for the input fields and add it to the main panel
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));

        // Add a Warehouse Name label to the inputPanel
        inputPanel.add(new JLabel("Warehouse Name:"));

        // Create a warehouse name text field
        JTextField warehouseNameField = new JTextField();

        // Add the warehouse name text field to the inputPanel
        inputPanel.add(warehouseNameField);

        // Add the inputPanel to the north of the warehousePanel
        warehousePanel.add(inputPanel, BorderLayout.NORTH);

        // Create a panel for the buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));

        // Create three buttons for Add, Edit & Delete
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        // Add the buttons to the buttonsPanel
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);

        // Add the buttonsPanel to the south of the categoryPanel
        warehousePanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add an ActionListener to the Add button
        addButton.addActionListener(e -> {

            // Get the entered warehouse name
            String warehouseName = warehouseNameField.getText();

            // Check if the entered name is not empty
            if (!warehouseName.trim().isEmpty()) {
                WarehouseDTO warehouseDTO = new WarehouseDTO();
                warehouseDTO.setName(warehouseName);
                warehouseController.createWarehouse(warehouseDTO);
                updateWarehouseTable(warehouseTableModel, warehouseController);
            }
        });

        // Add an ActionListener to the Edit button that opens a dialog to edit a selected warehouse and updates it in the table model
        editButton.addActionListener(e -> {

            // Get the selected row index
            int selectedRow = warehouseTable.getSelectedRow();

            // Check if a row is selected
            if (selectedRow != -1) {

                // Get the current category name from the table model
                String currentWarehouseName = (String) warehouseTableModel.getValueAt(selectedRow, 0);

                // Create a dialog to allow editing of the warehouse name
                String newWarehouseName = (String) JOptionPane.showInputDialog(
                        warehousePanel,
                        "Edit Warehouse Name:",
                        "Edit Warehouse",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        currentWarehouseName
                );

                // If the user has entered a new warehouse name, and it's different from the old one
                if (newWarehouseName != null && !newWarehouseName.trim().isEmpty()
                        && !newWarehouseName.equals(
                        currentWarehouseName)) {

                    // Update the category using the WarehouseController
                    warehouseController.updateWarehouse(currentWarehouseName, newWarehouseName);

                    // Update the JTable with new data
                    updateWarehouseTable(warehouseTableModel, warehouseController);
                }
            } else {
                // Show a warning message if no row is selected
                JOptionPane.showMessageDialog(warehousePanel, "Please select a row to edit.",
                        "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add an ActionListener for the deleteButton
        deleteButton.addActionListener(e -> {
            int selectedRow = warehouseTable.getSelectedRow();

            // Check if a row is selected
            if (selectedRow >= 0) {

                // Get the current category name from the table model
                String currentWarehouseName = (String) warehouseTableModel.getValueAt(selectedRow, 0);

                // Try to delete the category using the CategoryController
                try {
                    warehouseController.deleteWarehouse(currentWarehouseName);
                } catch (NotFoundException ex) {
                    // Throw the custom NotFoundException if the category is not found
                    throw new com.oop.bd.praktikum.exceptions.NotFoundException(ex.getMessage());
                }

                updateWarehouseTable(warehouseTableModel, warehouseController);
            }
        });
        return warehousePanel;
    }

    private void updateWarehouseTable(DefaultTableModel warehouseTableModel,
                                      WarehouseController warehouseController) {
        List<WarehouseDTO> warehouses = warehouseController.getAllWarehouses();
        warehouseTableModel.setRowCount(0);

        // Add the fetched warehouses to the table model
        for (WarehouseDTO warehouse : warehouses) {
            warehouseTableModel.addRow(new Object[]{warehouse.getName()});
        }
    }
}
