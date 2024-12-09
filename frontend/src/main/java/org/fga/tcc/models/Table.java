package org.fga.tcc.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.table.DefaultTableModel;
import java.awt.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Table {

    private Component table;

    private DefaultTableModel model;

}
