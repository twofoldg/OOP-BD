package com.oop.bd.praktikum.panel;

import com.oop.bd.praktikum.controller.CategoryController;
import com.oop.bd.praktikum.dto.CategoryDTO;
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

public class CategoryPanel {

  public JPanel createCategoryPanel(DefaultCellEditor editor, CategoryController categoryController) {
    JPanel categoryPanel = new JPanel(new BorderLayout());

    // Create and set up the JTable
    DefaultTableModel categoryTableModel = new DefaultTableModel(new Object[][]{},
        new String[]{"Name"});
    JTable categoryTable = new JTable(categoryTableModel);

    //Setting the editor in order to forbid double click editing of the rows
    categoryTable.setDefaultEditor(Object.class, editor);
    JScrollPane categoryTableScrollPane = new JScrollPane(categoryTable);
    categoryPanel.add(categoryTableScrollPane, BorderLayout.CENTER);

    // Create the input panel with JLabels and JTextFields
    JPanel inputPanel = new JPanel(new GridLayout(1, 2));
    inputPanel.add(new JLabel("Category Name:"));
    JTextField categoryNameField = new JTextField();
    inputPanel.add(categoryNameField);
    categoryPanel.add(inputPanel, BorderLayout.NORTH);

    // Create the buttons panel with JButtons for CRUD operations and search
    JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));
    JButton addButton = new JButton("Add");
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    buttonsPanel.add(addButton);
    buttonsPanel.add(editButton);
    buttonsPanel.add(deleteButton);
    categoryPanel.add(buttonsPanel, BorderLayout.SOUTH);

    //Add button listener
    addButton.addActionListener(e -> {
      String categoryName = categoryNameField.getText();
      if (!categoryName.trim().isEmpty()) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName(categoryName);
        categoryController.createCategory(categoryDTO);
        updateCategoryTable(categoryTableModel, categoryController);
      }
    });

    //Edit button listener
    editButton.addActionListener(e -> {
      int selectedRow = categoryTable.getSelectedRow();
      if (selectedRow != -1) {
        String currentCategoryName = (String) categoryTableModel.getValueAt(selectedRow, 0);

        // Create a dialog to allow the user to edit the category name
        String newCategoryName = (String) JOptionPane.showInputDialog(
            categoryPanel,
            "Edit Category Name:",
            "Edit Category",
            JOptionPane.PLAIN_MESSAGE,
            null,
            null,
            currentCategoryName
        );

        // If the user has entered a new category name and it's different from the old one
        if (newCategoryName != null &&
            !newCategoryName.trim().isEmpty() &&
            !newCategoryName.trim().equals(currentCategoryName.trim())) {
          CategoryDTO categoryDTO = categoryController.getCategoryByName(currentCategoryName);
          categoryDTO.setName(newCategoryName);
          categoryController.updateCategory(currentCategoryName, categoryDTO);

          // Update the JTable with new data
          updateCategoryTable(categoryTableModel, categoryController);
        }
      } else {
        JOptionPane.showMessageDialog(categoryPanel, "Please select a row to edit.",
            "No Row Selected", JOptionPane.WARNING_MESSAGE);
      }
    });

    //Delete button listener
    deleteButton.addActionListener(e -> {
      int selectedRow = categoryTable.getSelectedRow();

      if (selectedRow >= 0) {
        String currentCategoryName = (String) categoryTableModel.getValueAt(selectedRow, 0);

        try {
          categoryController.deleteCategory(currentCategoryName);
        } catch (NotFoundException ex) {
          throw new RuntimeException(ex.getMessage());
        }

        updateCategoryTable(categoryTableModel, categoryController);
      }
    });
    return categoryPanel;
  }

  private void updateCategoryTable(DefaultTableModel categoryTableModel, CategoryController categoryController) {
    List<CategoryDTO> categories = categoryController.getAllCategories();

    // Clear the table model
    categoryTableModel.setRowCount(0);

    // Add the fetched categories to the table model
    for (CategoryDTO category : categories) {
      categoryTableModel.addRow(new Object[]{category.getName()});
    }
  }

}
