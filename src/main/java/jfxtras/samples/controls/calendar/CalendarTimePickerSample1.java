package jfxtras.samples.controls.calendar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import jfxtras.internal.scene.control.skin.CalendarTimePickerSkin;
import jfxtras.samples.JFXtrasSampleBase;
import jfxtras.scene.control.CalendarTextField;
import jfxtras.scene.control.CalendarTimePicker;
import jfxtras.scene.layout.GridPane;
import jfxtras.scene.layout.VBox;

public class CalendarTimePickerSample1 extends JFXtrasSampleBase
{
    public CalendarTimePickerSample1() {
        calendarTimePicker = new CalendarTimePicker();
    }
    final CalendarTimePicker calendarTimePicker;

    @Override
    public String getSampleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getSampleDescription() {
        return "CalendarTimePicker is a time picker based on Java's Calendar class (hence the name CalendarTimePicker). \n"
        	 + "There are also an implementation available for Java 8 new date API like LocalTimePicker. "
        	 ;
    }

    @Override
    public Node getPanel(Stage stage) {
		this.stage = stage;

		VBox root = new VBox(20);
        root.setPadding(new Insets(30, 30, 30, 30));

        root.getChildren().addAll(calendarTimePicker);

        return root;
    }
	private Stage stage;

    @Override
    public Node getControlPanel() {
        // the result
        GridPane lGridPane = new GridPane();
        lGridPane.setVgap(2.0);
        lGridPane.setHgap(2.0);

        // setup the grid so all the labels will not grow, but the rest will
        ColumnConstraints lColumnConstraintsAlwaysGrow = new ColumnConstraints();
        lColumnConstraintsAlwaysGrow.setHgrow(Priority.ALWAYS);
        ColumnConstraints lColumnConstraintsNeverGrow = new ColumnConstraints();
        lColumnConstraintsNeverGrow.setHgrow(Priority.NEVER);
        lGridPane.getColumnConstraints().addAll(lColumnConstraintsNeverGrow, lColumnConstraintsAlwaysGrow);
        int lRowIdx = 0;


        // calendar
        {
            Label lLabel = new Label("Value");
            lGridPane.add(lLabel, new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            final CalendarTextField lCalendarTextField = new CalendarTextField();
            lCalendarTextField.setTooltip(new Tooltip("The currently selected time (single mode)"));
            lCalendarTextField.setDisable(true);
            lGridPane.add(lCalendarTextField, new GridPane.C().row(lRowIdx).col(1));
            lCalendarTextField.calendarProperty().bindBidirectional(calendarTimePicker.calendarProperty());
            lCalendarTextField.setDateFormat( SimpleDateFormat.getTimeInstance() );
        }
        lRowIdx++;

        // Locale
        {
            lGridPane.add(new Label("Locale"), new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            ObservableList<Locale> lLocales = FXCollections.observableArrayList(Locale.getAvailableLocales());
            FXCollections.sort(lLocales,  (o1, o2) -> { return o1.toString().compareTo(o2.toString()); } );
            ComboBox<Locale> lComboBox = new ComboBox<>( lLocales );
            lComboBox.converterProperty().set(new StringConverter<Locale>() {
                @Override
                public String toString(Locale locale) {
                    return locale == null ? "-"  : locale.toString();
                }

                @Override
                public Locale fromString(String s) {
                    if ("-".equals(s)) return null;
                    return new Locale(s);
                }
            });
            lComboBox.setEditable(true);
            lGridPane.add(lComboBox, new GridPane.C().row(lRowIdx).col(1));
            lComboBox.valueProperty().bindBidirectional(calendarTimePicker.localeProperty());
        }
        lRowIdx++;

		// stylesheet
		{		
			Label lLabel = new Label("Stage Stylesheet");
			lGridPane.add(lLabel, new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT).valignment(VPos.TOP));
			TextArea lTextArea = createTextAreaForCSS(stage, FXCollections.observableArrayList(
				".CalendarTimePicker {\n\t-fxx-show-ticklabels:YES; /* " +  Arrays.toString(CalendarTimePickerSkin.ShowTickLabels.values()) + " */\n}",
				".CalendarTimePicker {\n\t-fxx-label-dateformat:\"hh:mm a\"; /* See SimpleDateFormat, e.g. 'HH' for 24 hours per day */\n}", 
				".CalendarTimePicker {\n\t-fxx-label-dateformat:\"HH:mm:ss\"; /* See SimpleDateFormat, e.g. 'HH' for 24 hours per day */\n}") 
			);
			lGridPane.add(lTextArea, new GridPane.C().row(lRowIdx).col(1).vgrow(Priority.ALWAYS).minHeight(100.0));
		}
        lRowIdx++;

        // done
        return lGridPane;
    }

    @Override
    public String getJavaDocURL() {
		return "http://jfxtras.org/doc/8.0/jfxtras-controls/" + CalendarTimePicker.class.getName().replace(".", "/") + ".html";
    }

    public static void main(String[] args) {
        launch(args);
    }
}