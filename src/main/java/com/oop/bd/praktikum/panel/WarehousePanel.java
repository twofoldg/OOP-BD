package com.oop.bd.praktikum.panel;

import com.oop.bd.praktikum.controller.WarehouseController;
import com.oop.bd.praktikum.dto.WarehouseDTO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

public class WarehousePanel {

  public JPanel createWarehousePanel(DefaultCellEditor editor,
      WarehouseController warehouseController) {
    JPanel warehousePanel = new JPanel(new BorderLayout());

    DefaultTableModel warehouseTableModel = new DefaultTableModel(new Object[][]{},
        new String[]{"Name"});
    JTable warehouseTable = new JTable(warehouseTableModel);

    //Setting the editor in order to forbid double click editing of the rows
    warehouseTable.setDefaultEditor(Object.class, editor);
    JScrollPane warehouseTableScrollPane = new JScrollPane(warehouseTable);
    warehousePanel.add(warehouseTableScrollPane, BorderLayout.CENTER);

    JPanel inputPanel = new JPanel(new GridLayout(1, 2));
    inputPanel.add(new JLabel("Warehouse Name:"));
    JTextField warehouseNameField = new JTextField();
    inputPanel.add(warehouseNameField);
    warehousePanel.add(inputPanel, BorderLayout.NORTH);

    JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    buttonsPanel.add(addButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(deleteButton);
    warehousePanel.add(buttonsPanel, BorderLayout.SOUTH);

    addButton.addActionListener(e -> {
      String warehouseName = warehouseNameField.getText();
      if (!warehouseName.trim().isEmpty()) {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setName(warehouseName);
        warehouseController.createWarehouse(warehouseDTO);
        updateWarehouseTable(warehouseTableModel, warehouseController);
      }
    });

    editButton.addActionListener(e -> {
      int selectedRow = warehouseTable.getSelectedRow();
      if (selectedRow != -1) {
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

        // If the user has entered a new warehouse name and it's different from the old one
        if (newWarehouseName != null && !newWarehouseName.trim().isEmpty()
            && !newWarehouseName.equals(
            currentWarehouseName)) {
          WarehouseDTO warehouseDTO = warehouseController.getWarehouseByName(currentWarehouseName);
          warehouseDTO.setName(newWarehouseName);
          warehouseController.updateWarehouse(currentWarehouseName, warehouseDTO);

          updateWarehouseTable(warehouseTableModel, warehouseController);
        }
      } else {
        JOptionPane.showMessageDialog(warehousePanel, "Please select a row to edit.",
            "No Row Selected", JOptionPane.WARNING_MESSAGE);
      }
    });

    deleteButton.addActionListener(e -> {
      int selectedRow = warehouseTable.getSelectedRow();

      if (selectedRow >= 0) {
        String currentWarehouseName = (String) warehouseTableModel.getValueAt(selectedRow, 0);

        try {
          warehouseController.deleteWarehouse(currentWarehouseName);
        } catch (NotFoundException ex) {
          throw new RuntimeException(ex.getMessage());
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
