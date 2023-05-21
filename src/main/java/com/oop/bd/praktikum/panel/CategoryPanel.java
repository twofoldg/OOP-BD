package com.oop.bd.praktikum.panel;

import com.oop.bd.praktikum.controller.CategoryController;
import com.oop.bd.praktikum.dto.CategoryDTO;

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

public class CategoryPanel {

    public JPanel createCategoryPanel(DefaultCellEditor editor, CategoryController categoryController) {
        // Create a new JPanel with BorderLayout
        JPanel categoryPanel = new JPanel(new BorderLayout());

        // Create a DefaultTableModel for a JTable
        DefaultTableModel categoryTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Name"});

        // Create a JTable with the specified table model
        JTable categoryTable = new JTable(categoryTableModel);

        // Set the custom editor to disable editing with double-click on a rows
        categoryTable.setDefaultEditor(Object.class, editor);

        // Create a category table scroll pane for the table
        JScrollPane categoryTableScrollPane = new JScrollPane(categoryTable);

        // Add the category table scroll pane to the center of the categoryPanel
        categoryPanel.add(categoryTableScrollPane, BorderLayout.CENTER);

        // Create an inputPanel
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));

        // Add a Category Name label to the inputPanel
        inputPanel.add(new JLabel("Category Name:"));

        // Create a category name text field
        JTextField categoryNameField = new JTextField();

        // Add the category name text field to the inputPanel
        inputPanel.add(categoryNameField);

        // Add the inputPanel to the north of the categoryPanel
        categoryPanel.add(inputPanel, BorderLayout.NORTH);

        // Create a buttons panel
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
        categoryPanel.add(buttonsPanel, BorderLayout.SOUTH);

        // Add an ActionListener for the addButton
        addButton.addActionListener(e -> {

            // Get the entered category name
            String categoryName = categoryNameField.getText();

            // Check if the entered name is not empty
            if (!categoryName.trim().isEmpty()) {
                CategoryDTO categoryDTO = new CategoryDTO();
                categoryDTO.setName(categoryName);
                categoryController.createCategory(categoryDTO);
                updateCategoryTable(categoryTableModel, categoryController);
            }
        });

        // Add an ActionListener for the editButton
        editButton.addActionListener(e -> {

            // Get the selected row index
            int selectedRow = categoryTable.getSelectedRow();

            // Check if a row is selected
            if (selectedRow != -1) {

                // Get the current category name from the table model
                String currentCategoryName = (String) categoryTableModel.getValueAt(selectedRow, 0);

                // Show an input dialog to enter the new category name
                String newCategoryName = (String) JOptionPane.showInputDialog(
                        categoryPanel,
                        "Edit Category Name:",
                        "Edit Category",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        currentCategoryName
                );

                // Check if a new name is entered and if it's different from the old one
                if (newCategoryName != null &&
                        !newCategoryName.trim().isEmpty() &&
                        !newCategoryName.trim().equals(currentCategoryName.trim())) {

                    // Update the category using the CategoryController
                    categoryController.updateCategory(currentCategoryName, newCategoryName);

                    // Update the JTable with new data
                    updateCategoryTable(categoryTableModel, categoryController);
                }
            } else {
                // Show a warning message if no row is selected
                JOptionPane.showMessageDialog(categoryPanel, "Please select a row to edit.",
                        "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Add an ActionListener for the deleteButton
        deleteButton.addActionListener(e -> {
            int selectedRow = categoryTable.getSelectedRow();

            // Check if a row is selected
            if (selectedRow >= 0) {

                // Get the current category name from the table model
                String currentCategoryName = (String) categoryTableModel.getValueAt(selectedRow, 0);

                // Try to delete the category using the CategoryController
                categoryController.deleteCategory(currentCategoryName);

                // Update the JTable with new data
                updateCategoryTable(categoryTableModel, categoryController);
            }
        });
        // Return the created categoryPanel
        return categoryPanel;
    }

    private void updateCategoryTable(DefaultTableModel categoryTableModel, CategoryController categoryController) {
        // Get all the categories from the database
        List<CategoryDTO> categories = categoryController.getAllCategories();

        // Reset the table row count
        categoryTableModel.setRowCount(0);

        // Iterate through the fetched categories and add them to the table model
        for (CategoryDTO category : categories) {
            categoryTableModel.addRow(new Object[]{category.getName()});
        }
    }
}
