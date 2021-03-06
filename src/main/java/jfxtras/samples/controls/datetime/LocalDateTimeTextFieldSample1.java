package jfxtras.samples.controls.datetime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxtras.internal.scene.control.skin.CalendarPickerControlSkin;
import jfxtras.internal.scene.control.skin.ListSpinnerSkin;
import jfxtras.samples.JFXtrasSampleBase;
import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.layout.GridPane;
import jfxtras.scene.layout.HBox;
import jfxtras.scene.layout.VBox;

public class LocalDateTimeTextFieldSample1 extends JFXtrasSampleBase
{
    public LocalDateTimeTextFieldSample1() {
        localDateTimeTextField = new LocalDateTimeTextField();
    }
    final LocalDateTimeTextField localDateTimeTextField;

    @Override
    public String getSampleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getSampleDescription() {
        return "Basic LocalDateTimeTextField usage";
    }

    @Override
    public Node getPanel(Stage stage) {
		this.stage = stage;
		
        VBox root = new VBox(20);
        root.setPadding(new Insets(30, 30, 30, 30));

        root.getChildren().addAll(localDateTimeTextField);

		localDateTimeTextField.parseErrorCallbackProperty().set( (Callback<Throwable, Void>) (Throwable p) -> {
			showPopup(localDateTimeTextField, "Parse error: " + p.getLocalizedMessage());
			return null;
		});

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

        // allowNull
        {
            Label lLabel = new Label("Null allowed");
            lGridPane.add(lLabel, new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            CheckBox lCheckBox = new CheckBox();
            lCheckBox.setTooltip(new Tooltip("Is the control allowed to hold null (or have no calendar deselected)"));
            lGridPane.add(lCheckBox, new GridPane.C().row(lRowIdx).col(1));
            lCheckBox.selectedProperty().bindBidirectional(localDateTimeTextField.allowNullProperty());
        }
        lRowIdx++;

        // Locale
        {
            lGridPane.add(new Label("Locale"), new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            final ObservableList<Locale> lLocales = FXCollections.observableArrayList(Locale.getAvailableLocales());
            FXCollections.sort(lLocales,  (o1, o2) -> { return o1.toString().compareTo(o2.toString()); } );
            localeComboBox = new ComboBox( lLocales );
            localeComboBox.converterProperty().set(new StringConverter<Locale>() {
				@Override
				public String toString(Locale locale) {
					return locale == null ? "-" : locale.toString();
				}

				@Override
				public Locale fromString(String s) {
					if ("-".equals(s)) return null;
					// this goes wrong with upper and lowercase, so we do a toString search in the list: return new Locale(s);
					for (Locale l : lLocales) {
						if (l.toString().equalsIgnoreCase(s)) {
							return l;
						}
					}
					throw new IllegalArgumentException(s);
				}
			});
            localeComboBox.setEditable(true);
            lGridPane.add(localeComboBox, new GridPane.C().row(lRowIdx).col(1));
			// once the date format has been set manually, changing the local has no longer any effect, so binding the property is useless
			localeComboBox.valueProperty().addListener( (observable) -> {
				localDateTimeTextField.setLocale(determineLocale());
			});
        }
        lRowIdx++;

        // date time format
        {
            Label lLabel = new Label("DateTime formatter");
            lGridPane.add(lLabel, new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            TextField lDateTimeFormatterTextField = new TextField();
            lDateTimeFormatterTextField.setTooltip(new Tooltip("A DateTimeFormatter used to render and parse the text"));
            lGridPane.add(lDateTimeFormatterTextField, new GridPane.C().row(lRowIdx).col(1));
            lDateTimeFormatterTextField.focusedProperty().addListener( (observable) -> {
            	localDateTimeTextField.setDateTimeFormatter( lDateTimeFormatterTextField.getText().length() == 0 ? null : DateTimeFormatter.ofPattern(lDateTimeFormatterTextField.getText()).withLocale(determineLocale()) );
			});
        }
        lRowIdx++;

        // DateTimeFormatters
        {
			lRowIdx = addObservableListManagementControlsToGridPane("Parse only formatters", "Alternate DateTimeFormatters patterns only for parsing the typed text", lGridPane, lRowIdx, localDateTimeTextField.dateTimeFormattersProperty(), (String s) -> {
				Locale lLocale = localeComboBox.valueProperty().get();
				if (lLocale == null) {
					lLocale = Locale.getDefault();
				}
				return DateTimeFormatter.ofPattern(s).withLocale(lLocale);
			});
        }

        // highlight
		{
			lRowIdx = addObservableListManagementControlsToGridPane("Highlighted", "All highlighted dates", lGridPane, lRowIdx, localDateTimeTextField.highlightedLocalDateTimes(), new LocalDateTimeTextField()
				, (Control c) -> {
					LocalDateTimeTextField lLocalDateTimeTextField = (LocalDateTimeTextField)c;
					LocalDateTime lLocalDateTime = lLocalDateTimeTextField.getLocalDateTime();
					lLocalDateTimeTextField.setLocalDateTime(null);
					return lLocalDateTime;
				}
				, (LocalDateTime t) -> {
					return t == null ? "" : DateTimeFormatter.ISO_DATE_TIME.format(t);
				}
			);
		}

        // disabled
		{
			lRowIdx = addObservableListManagementControlsToGridPane("Disabled", "All disabled dates", lGridPane, lRowIdx, localDateTimeTextField.disabledLocalDateTimes(), new LocalDateTimeTextField()
				, (Control c) -> {
					LocalDateTimeTextField lLocalDateTimeTextField = (LocalDateTimeTextField)c;
					LocalDateTime lLocalDateTime = lLocalDateTimeTextField.getLocalDateTime();
					lLocalDateTimeTextField.setLocalDateTime(null);
					return lLocalDateTime;
				}
				, (LocalDateTime t) -> {
					return t == null ? "" : DateTimeFormatter.ISO_DATE_TIME.format(t);
				}
			);
		}

        // localDateTimeRangeCallback
        {
            Label lLabel = new Label("Range callback");
            lGridPane.add(lLabel, new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT));
            HBox lHBox = new HBox();
            lGridPane.add(lHBox, new GridPane.C().row(lRowIdx).col(1));
            final CheckBox lCheckBox = new CheckBox();
            lHBox.add(lCheckBox);
            lCheckBox.setTooltip(new Tooltip("Register a callback and show what the range change data is"));
            final TextField lTextField = new TextField();
            lHBox.add(lTextField, new HBox.C().hgrow(Priority.ALWAYS));
            lCheckBox.selectedProperty().addListener( (invalidationEvent) -> {
            	if (lCheckBox.selectedProperty().get()) {
            		localDateTimeTextField.setLocalDateTimeRangeCallback( (range) -> {
            			lTextField.setText(range.getStartLocalDateTime() + " - " + range.getEndLocalDateTime());
						return null;
					});
            	}
            	else {
            		localDateTimeTextField.setLocalDateTimeRangeCallback(null);
        			lTextField.setText("");
            	}
            });
        }
        lRowIdx++;
        
		// stylesheet
		{		
			Label lLabel = new Label("Stage Stylesheet");
			lGridPane.add(lLabel, new GridPane.C().row(lRowIdx).col(0).halignment(HPos.RIGHT).valignment(VPos.TOP));
			TextArea lTextArea = createTextAreaForCSS(stage, FXCollections.observableArrayList(
				".LocalDateTimePicker {\n\t-fxx-show-weeknumbers:NO; /* " +  Arrays.toString(CalendarPickerControlSkin.ShowWeeknumbers.values()) + " */\n}",
				".ListSpinner {\n\t-fxx-arrow-position:SPLIT; /* " + Arrays.toString(ListSpinnerSkin.ArrowPosition.values()) + " */ \n}",
				".ListSpinner {\n\t-fxx-arrow-direction:VERTICAL; /* " + Arrays.toString(ListSpinnerSkin.ArrowDirection.values()) + " */ \n}"));
			lGridPane.add(lTextArea, new GridPane.C().row(lRowIdx).col(1).vgrow(Priority.ALWAYS).minHeight(100.0));
		}
        lRowIdx++;

        // done
        return lGridPane;
    }
 	private ComboBox<Locale> localeComboBox;

	private Locale determineLocale() {
		Locale lLocale = localeComboBox.valueProperty().get();
		if (lLocale == null) {
			lLocale = Locale.getDefault();
		}
		return lLocale;
	}
	
    @Override
    public String getJavaDocURL() {
		return "http://jfxtras.org/doc/8.0/jfxtras-controls/" + LocalDateTimeTextField.class.getName().replace(".", "/") + ".html";
    }

    public static void main(String[] args) {
        launch(args);
    }
}