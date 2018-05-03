/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opendiabetesvaultgui.slice;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import opendiabetesvaultgui.launcher.FatherController;

/**
 *
 * @author Daniel Schäfer, Martin Steil, Julian Schwind, Kai Worsch
 */
public class SliceController extends FatherController implements Initializable{
    
    @FXML
    private ListView<String> listviewfilterelements ;
    
    
    @FXML
    private void doFilter(ActionEvent event){
        
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        //addItems to listview
        ObservableList<String> items = listviewfilterelements.getItems();
        items.add("Test");
        items.add("Test1");
        items.add("Test2");
        items.add("Test3");
        items.add("Test4");
        items.add("Test5");
        
    }

    
    
}
