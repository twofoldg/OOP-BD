package com.oop.bd.praktikum;

import com.oop.bd.praktikum.controller.CategoryController;
import com.oop.bd.praktikum.controller.ProductController;
import com.oop.bd.praktikum.controller.WarehouseController;
import com.oop.bd.praktikum.dto.CategoryDTO;
import com.oop.bd.praktikum.dto.ProductDTO;
import com.oop.bd.praktikum.dto.WarehouseDTO;
import com.oop.bd.praktikum.entity.Category;
import com.oop.bd.praktikum.entity.Product;
import com.oop.bd.praktikum.entity.Warehouse;
import com.oop.bd.praktikum.panel.CategoryPanel;
import com.oop.bd.praktikum.panel.WarehousePanel;
import com.oop.bd.praktikum.service.CategoryService;
import com.oop.bd.praktikum.service.ProductService;
import com.oop.bd.praktikum.service.WarehouseService;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

@Component
public class PraktikumApplication {

    private final CategoryController categoryController;
    private final WarehouseController warehouseController;
    private final ProductController productController;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final WarehouseService warehouseService;

    // A custom editor to disable editing with double-click on a rows
    DefaultCellEditor editor = new DefaultCellEditor(new JTextField()) {
        @Override
        public boolean isCellEditable(EventObject e) {
            return false;
        }
    };


    // A custom DocumentFilter that allows only integers to be entered.
    // If the new string contains only digits, the methods call the methods insertString/Replace to insert the integers.
    DocumentFilter integerFilter = new DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs)
                throws BadLocationException {
            if (string != null && string.matches("\\d*")) {
                super.replace(fb, offset, length, string, attrs);
            }
        }
    };
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> warehouseComboBox;

    public PraktikumApplication(CategoryController categoryController,
                                WarehouseController warehouseController,
                                ProductController productController,
                                ProductService productService,
                                CategoryService categoryService,
                                WarehouseService warehouseService) {
        this.categoryController = categoryController;
        this.warehouseController = warehouseController;
        this.productController = productController;
        this.productService = productService;
        this.categoryService = categoryService;
        this.warehouseService = warehouseService;
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            //title
            JFrame mainFrame = new JFrame("Warehouse Management System");
            //Ends the application on close of the modal
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(1000, 550);
            mainFrame.setResizable(false);

            JTabbedPane tabbedPane = new JTabbedPane();
            CategoryPanel categoryPanel = new CategoryPanel();
            WarehousePanel warehousePanel = new WarehousePanel();

            //Adds the 3 needed tabs
            tabbedPane.addTab("Categories",
                    categoryPanel.createCategoryPanel(editor, categoryController));
            tabbedPane.addTab("Products", createProductPanel());
            tabbedPane.addTab("Warehouses",
                    warehousePanel.createWarehousePanel(editor, warehouseController));

            //If the tab is Products load the information from the category and the warehouse tables
            tabbedPane.addChangeListener(e -> {
                if (tabbedPane.getSelectedIndex() == 1) { // If the selected tab is Products
                    fillCategoryComboBox(categoryComboBox); // Use the reference to the combo box
                    fillWarehouseComboBox(warehouseComboBox);
                }
            });

            mainFrame.add(tabbedPane);
            mainFrame.setVisible(true);
        });
    }

    private JPanel createProductPanel() {
        // Create a productPanel
        JPanel productPanel = new JPanel(new BorderLayout());

        // Create a table model with the specified columns: "Name", "Quantity", "Category", "Warehouse"
        DefaultTableModel productTableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"Name", "Quantity", "Category", "Warehouse"});

        // Create a productTable using the table model
        JTable productTable = new JTable(productTableModel);

        // Set the editor in order to forbid double click editing of the rows
        productTable.setDefaultEditor(Object.class, editor);

        // Create a product table scroll pane for the table and add it to the product panel
        JScrollPane productTableScrollPane = new JScrollPane(productTable);
        productPanel.add(productTableScrollPane, BorderLayout.CENTER);

        // Create an input panel with a GridLayout
        JPanel inputPanel = new JPanel(new GridLayout(1, 8));

        // Add product name input components
        inputPanel.add(new JLabel("Product Name:"));
        JTextField productNameField = new JTextField();
        inputPanel.add(productNameField);

        // Add product quantity input components
        inputPanel.add(new JLabel("Quantity:"));
        JTextField productQuantityField = new JTextField();
        ((PlainDocument) productQuantityField.getDocument()).setDocumentFilter(integerFilter);
        inputPanel.add(productQuantityField);

        // Add category input components
        inputPanel.add(new JLabel("Category:"));
        this.categoryComboBox = new JComboBox<>();
        inputPanel.add(categoryComboBox);

        // Add warehouse input components
        inputPanel.add(new JLabel("Warehouse:"));
        this.warehouseComboBox = new JComboBox<>();
        inputPanel.add(warehouseComboBox);

        // Add inputPanel to the product panel
        productPanel.add(inputPanel, BorderLayout.NORTH);

        // Create a refresh button and add an ActionListener
        JButton refreshButton = new JButton("Reload table");
        refreshButton.addActionListener(e -> {
            updateProductTable(productTableModel);
        });
        inputPanel.add(refreshButton);

        // Create a buttons panel with a GridLayout
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3));

        // Create buttons for add, edit, search, and delete
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton searchButton = new JButton("Search");
        JButton deleteButton = new JButton("Delete");

        // Add the buttons to the buttons panel
        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(searchButton);
        buttonsPanel.add(deleteButton);

        // Add the buttons panel to the product panel
        productPanel.add(buttonsPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String productName = productNameField.getText();
            String productQuantity = productQuantityField.getText();
            String categoryName = (String) categoryComboBox.getSelectedItem();
            String warehouseName = (String) warehouseComboBox.getSelectedItem();

            if (!productName.trim().isEmpty() && !productQuantity.trim().isEmpty() &&
                    categoryName != null && warehouseName != null) {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(productName);
                productDTO.setQuantity(Integer.parseInt(productQuantity));
                productDTO.setCategoryName(categoryName);
                productDTO.setWarehouseName(warehouseName);

                productController.createProduct(productDTO);
                updateProductTable(productTableModel);
            }
        });

        editButton.addActionListener(e -> {

            // Retrieve all categories from the categoryController as a stream,
            // map each CategoryDTO to its name, and collect the names into a new String array
            String[] categories = categoryController.getAllCategories().stream()
                    .map(CategoryDTO::getName)
                    .toArray(String[]::new);

            // Retrieve all warehouses from the warehouseController as a stream,
            // map each WarehouseDTO to its name, and collect the names into a new String array
            String[] warehouses = warehouseController.getAllWarehouses().stream()
                    .map(WarehouseDTO::getName)
                    .toArray(String[]::new);


            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                String currentProductName = (String) productTableModel.getValueAt(selectedRow, 0);
                ProductDTO productDTO = productController.getProductByName(currentProductName);

                JDialog editProductDialog = new JDialog((Frame) null, "Edit Product", true);
                editProductDialog.setSize(300, 200);
                editProductDialog.setLayout(new GridLayout(5, 2));

                //Create a label and a text field for the name and add them to the product dialog.
                JLabel nameLabel = new JLabel("Name:");
                JTextField nameField = new JTextField(productDTO.getName());
                editProductDialog.add(nameLabel);
                editProductDialog.add(nameField);

                //Create a label and a text field for the quantity and add them to the product dialog.
                JLabel quantityLabel = new JLabel("Quantity:");
                JTextField quantityField = new JTextField(Integer.toString(productDTO.getQuantity()));
                ((PlainDocument) quantityField.getDocument()).setDocumentFilter(integerFilter);
                editProductDialog.add(quantityLabel);
                editProductDialog.add(quantityField);

                //Create a label and a text field for the category and add them to the product dialog.
                JLabel categoryLabel = new JLabel("Category:");
                String[] dialogCategories = Arrays.copyOf(categories, categories.length);
                JComboBox<String> categoryEditBox = new JComboBox<>(dialogCategories);
                categoryEditBox.setSelectedItem(productDTO.getCategoryName());
                editProductDialog.add(categoryLabel);
                editProductDialog.add(categoryEditBox);

                //Create a label and a text field for the warehouse and add them to the product dialog.
                JLabel warehouseLabel = new JLabel("Warehouse:");
                String[] dialogWarehouses = Arrays.copyOf(warehouses, warehouses.length);
                JComboBox<String> warehouseEditBox = new JComboBox<>(dialogWarehouses);
                warehouseEditBox.setSelectedItem(productDTO.getWarehouseName());
                editProductDialog.add(warehouseLabel);
                editProductDialog.add(warehouseEditBox);

                // Add OK and Cancel buttons
                JButton okButton = new JButton("OK");
                JButton cancelButton = new JButton("Cancel");
                editProductDialog.add(okButton);
                editProductDialog.add(cancelButton);

                // OK button listener
                okButton.addActionListener(event -> {
                    String newName = nameField.getText().trim();
                    int newQuantity = Integer.parseInt(quantityField.getText().trim());
                    String newCategoryName = (String) categoryEditBox.getSelectedItem();
                    String newWarehouseName = (String) warehouseEditBox.getSelectedItem();

                    if (!newName.isEmpty() && newQuantity >= 0 && !newCategoryName.isEmpty()
                            && !newWarehouseName.isEmpty()) {
                        productDTO.setName(newName);
                        productDTO.setQuantity(newQuantity);
                        productDTO.setCategoryName(newCategoryName);
                        productDTO.setWarehouseName(newWarehouseName);

                        productController.updateProductByName(currentProductName, productDTO);

                        // Update the JTable with new data
                        updateProductTable(productTableModel);

                        // Close the edit dialog
                        editProductDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(editProductDialog,
                                "All fields must be filled out correctly.",
                                "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    }
                });

                // Cancel button listener
                cancelButton.addActionListener(event -> editProductDialog.dispose());

                editProductDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(productPanel, "Please select a row to edit.",
                        "No Row Selected", JOptionPane.WARNING_MESSAGE);
            }
        });

        searchButton.addActionListener(e -> {
            // Creates a custom JDialog with input components for each search criteria
            JDialog searchProductDialog = new JDialog((Frame) null, "Search Product", true);
            searchProductDialog.setSize(300, 200);
            searchProductDialog.setLayout(new GridLayout(5, 2));

            // Creates and adds input components for each criteria
            JLabel nameLabel = new JLabel("Name:");
            JTextField nameField = new JTextField();
            searchProductDialog.add(nameLabel);
            searchProductDialog.add(nameField);

            JLabel quantityLabel = new JLabel("Quantity:");
            JTextField quantityField = new JTextField();
            ((PlainDocument) quantityField.getDocument()).setDocumentFilter(integerFilter);
            searchProductDialog.add(quantityLabel);
            searchProductDialog.add(quantityField);

            JLabel categoryLabel = new JLabel("Category:");
            JComboBox<String> categorySearchBox = new JComboBox<>(
                    categoryController.getAllCategories().stream()
                            .map(CategoryDTO::getName)
                            .toArray(String[]::new));
            categorySearchBox.insertItemAt("", 0);
            categorySearchBox.setSelectedIndex(0);
            searchProductDialog.add(categoryLabel);
            searchProductDialog.add(categorySearchBox);

            JLabel warehouseLabel = new JLabel("Warehouse:");
            JComboBox<String> warehouseSearchBox = new JComboBox<>(
                    warehouseController.getAllWarehouses().stream()
                            .map(WarehouseDTO::getName)
                            .toArray(String[]::new));
            warehouseSearchBox.insertItemAt("", 0);
            warehouseSearchBox.setSelectedIndex(0);
            searchProductDialog.add(warehouseLabel);
            searchProductDialog.add(warehouseSearchBox);

            // Adding of OK and Cancel buttons
            JButton okButton = new JButton("Search");
            JButton cancelButton = new JButton("Cancel");
            searchProductDialog.add(okButton);
            searchProductDialog.add(cancelButton);

            // OK button listener
            okButton.addActionListener(event -> {
                String searchName = nameField.getText().trim();
                Integer searchQuantity;
                if (quantityField.getText().trim().isEmpty()) {
                    searchQuantity = null;
                } else {
                    searchQuantity = Integer.valueOf(quantityField.getText().trim());
                }
                String searchCategory = (String) categorySearchBox.getSelectedItem();
                String searchWarehouse = (String) warehouseSearchBox.getSelectedItem();

                // Search for products using the input values
                List<ProductDTO> filteredProducts = productController.searchProducts(searchName,
                        searchQuantity, searchCategory, searchWarehouse);

                // Update the JTable with the filtered results
                updateProductTable(productTableModel, filteredProducts);

                // Close the search dialog
                searchProductDialog.dispose();
            });

            // Cancel button listener
            cancelButton.addActionListener(event -> searchProductDialog.dispose());

            searchProductDialog.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();

            if (selectedRow >= 0) {
                String currentProductName = (String) productTableModel.getValueAt(selectedRow, 0);
                String currentProductCategoryName = (String) productTableModel.getValueAt(selectedRow, 2);
                String currentProductWarehouseName = (String) productTableModel.getValueAt(selectedRow, 3);

                Category currentCategory = categoryService.findCategoryByName(currentProductCategoryName);
                Warehouse currentWarehouse = warehouseService.findWarehouseByName(
                        currentProductWarehouseName);
                Product currentProduct =
                        productService.findProductByNameAndCategoryAndWarehouse(currentProductName,
                                currentCategory, currentWarehouse);
                productController.deleteProduct(currentProduct.getId());

                updateProductTable(productTableModel);
            }
        });

        return productPanel;
    }

    // This method fills with all the available categories fetched from a CategoryController instance.
    private void fillCategoryComboBox(JComboBox<String> categoryComboBox) {
        categoryComboBox.removeAllItems(); // remove all existing items in the combo box
        List<CategoryDTO> categories = categoryController.getAllCategories(); // fetch all categories

        // iterate through each category and add its name to the combo box
        for (CategoryDTO category : categories) {
            categoryComboBox.addItem(category.getName());
        }
    }

    // This method fills with all the available warehouses fetched from a WarehouseController instance.
    private void fillWarehouseComboBox(JComboBox<String> warehouseComboBox) {
        // remove all existing items in the combo box
        warehouseComboBox.removeAllItems();

        // fetch all warehouses
        List<WarehouseDTO> warehouses = warehouseController.getAllWarehouses();

        // iterate through each warehouse and add its name to the combo box
        for (WarehouseDTO warehouse : warehouses) {
            warehouseComboBox.addItem(warehouse.getName());
        }
    }

    // This method updates with all the available products fetched from a ProductController instance.
    private void updateProductTable(DefaultTableModel productTableModel) {
        // fetch all products
        List<ProductDTO> products = productController.getAllProducts();

        // remove all existing rows from the table model
        productTableModel.setRowCount(0);


        // iterate through each product and add its information to a new row in the table model
        for (ProductDTO product : products) {
            productTableModel.addRow(
                    new Object[]{product.getName(), product.getQuantity(), product.getCategoryName(),
                            product.getWarehouseName()});
        }
    }

    // This method updates a JTable with the given products, which are filtered products fetched from a ProductController instance.
    private void updateProductTable(DefaultTableModel productTableModel, List<ProductDTO> products) {

        // remove all existing rows from the table model
        productTableModel.setRowCount(0);

        // iterate through each filtered product and add its information to a new row in the table model
        for (ProductDTO product : products) {
            Object[] rowData = new Object[]{
                    product.getName(),
                    product.getQuantity(),
                    product.getCategoryName(),
                    product.getWarehouseName()
            };
            productTableModel.addRow(rowData);
        }
    }
}