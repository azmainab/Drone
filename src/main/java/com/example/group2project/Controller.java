package com.example.group2project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private TreeView<String> itemsDropdown;
    @FXML
    private ImageView itemImage;
    @FXML
    private Text itemImageLabel;

    public void showItem() {
        TreeItem<String> selectedItem = itemsDropdown.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            System.out.println(selectedItem.getValue());
            URL res = Main.class.getResource(String.format("images/%s.jpg", selectedItem.getValue()));
            if (res != null)
                itemImage.setImage(new Image(String.valueOf(res)));
            itemImageLabel.setText(selectedItem.getValue());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<TreeItem<String>> fieldItems = new ArrayList<>();
        for (int i = 1; i <= 4; i++){
            fieldItems.add(new TreeItem<>(String.format("field %d", i)));
        }
        TreeItem<String> fields = new TreeItem<>("Fields");
        TreeItem<String> crops = new TreeItem<>("Crops");
        fields.getChildren().addAll(fieldItems);
        crops.getChildren().add(fields);
        itemsDropdown.setRoot(crops);
        itemsDropdown.setEditable(true);
        itemsDropdown.setCellFactory(p -> new TextFieldTreeCellImpl());

        itemImageLabel.setText("Please Select a Field");
    }
    private static final class TextFieldTreeCellImpl extends TreeCell<String> {
        private TextField textField;
        private final ContextMenu menu = new ContextMenu();
        private final ContextMenu leafMenu = new ContextMenu();
        public TextFieldTreeCellImpl() {
            MenuItem addMenuItem = new MenuItem("Add");
            MenuItem deleteMenuItem = new MenuItem("Delete");
            MenuItem renameMenuItem = new MenuItem("Rename");
            menu.getItems().addAll(addMenuItem, deleteMenuItem);
            leafMenu.getItems().addAll(deleteMenuItem, renameMenuItem);
            addMenuItem.setOnAction(actionEvent -> getTreeItem().getChildren().add(new TreeItem<>("new field")));
            deleteMenuItem.setOnAction(actionEvent -> {
                String curItemValue = getTreeItem().getValue();
                getTreeItem().getParent().getChildren().removeIf(x -> x.getValue().contentEquals(curItemValue));
            });
            renameMenuItem.setOnAction(actionEvent -> startEdit());
        }
        @Override
        public void startEdit() {
            super.startEdit();
            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setGraphic(getTreeItem().getGraphic());
        }
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null)
                        setContextMenu(menu);
                    else if (getTreeItem().isLeaf())
                        setContextMenu(leafMenu);
                }
            }
        }
        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });
        }
        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }
}
