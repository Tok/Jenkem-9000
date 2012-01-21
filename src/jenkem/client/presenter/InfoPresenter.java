package jenkem.client.presenter;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class InfoPresenter extends AbstractTabPresenter implements Presenter {
    private final Display display;

    public interface Display {
        FlexTable getInfoList();
        Widget asWidget();
    }

    public InfoPresenter(final HandlerManager eventBus,
            final TabPanel tabPanel, final Display view) {
        super(eventBus, tabPanel);
        this.display = view;
    }

    public void bind() {
    }

    @Override
    public final void go(final HasWidgets container) {
        bind();
        prepareTable();
        container.clear();
        container.add(super.getTabPanel());
    }

    //formatter:off
    private void prepareTable() {
        display.getInfoList().removeAllRows();
        display.getInfoList().getColumnFormatter().setWidth(0, "100px");

        int row = 0;
        display.getInfoList().setText(row, 0,
                "Welcome to the Jenkem 9000 ASCII converter.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0,"To convert an image, go to the main tab, enter a link to the image and click the convert button. Please be patient. It takes a few seconds before the output appears. If you make a good conversion, you can add it to the gallery, by clicking the submit button. Other images with the same name will be repalced. You can save the HTML output from the gallery after submitting your conversion. If you do so, don't forget to also save the provided CSS and fix the stylesheet link in your HTML source if you want to upload it somewhere else.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Problems:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "Browsers:");
        display.getInfoList().getCellFormatter()
                .setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
        display.getInfoList()
                .setText(row, 1, "This version of Jenkem 9000 doesn't work right with Internet Explorer, Lynx and unfortunately Opera. For best results use Firefox or Chrome. Safari may also work, but isn't tested.");
        row++;
        display.getInfoList().setText(row, 0, "IRC Clients:");
        display.getInfoList().getCellFormatter()
                .setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
        display.getInfoList().setText(row, 1, "Some IRC clients limit the input that can be put on one line. Depending on your client, long lines with many color changes and therefore too much data will be cut off or skipped to a new line and thereby break the image. If this happens, you might try to use a lower setting for the line-width of your conversion. It's often a good idea to test replaying your converted image into an empty channel before flooding it directly into a channel full with people idling in it.");
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Conversion Modes:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setText(row, 0, "Super-Hybrid:");
        display.getInfoList().setText(row, 1, "This is the default method and combines the best of all other methods. The output is anti aliased on the ASCII level, taking a sample of four Pixels.");
        row++;
        display.getInfoList().setText(row, 0, "Full HD:");
        display.getInfoList().setText(row, 1, "Tanslates the image pixel by pixel. It doesn't have any anti aliasing on the ASCII level. This mode is best suited for use with an ANSII character set.");
        row++;
        display.getInfoList().setText(row, 0, "Pwntari:");
        display.getInfoList().setText(row, 1, "Doesn't use any ASCII palette. The output is only using the character \"_\" and creates the image by combining background- and foreground colors.");
        row++;
        display.getInfoList().setText(row, 0, "Hybrid:");
        display.getInfoList().setText(row, 1, "Combines the plain method with the Full HD method. But uses anti aliasing on the ASCII level.");
        row++;
        display.getInfoList().setText(row, 0, "Plain:");
        display.getInfoList().setText(row, 1, "Doesn't output any colors. This method is best used with black & white images like stencils.");
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Line width:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0,"Allows setting how many visible characters the converted image should have per line. Invisible characters like IRC color codes are not counted.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Color Schemes:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "Default:");
        display.getInfoList().setText(row, 1, "The default color schema. Works best for fotos and colorful images.");
        row++;
        display.getInfoList().setText(row, 0, "Old:");
        display.getInfoList().setText(row, 1, "Another color schema, that was in place before the default was optimized. The output looks similar to conversions with the default scheme.");
        row++;
        display.getInfoList().setText(row, 0, "Vivid:");
        display.getInfoList().setText(row, 1, "A color schema with more vivid colors, by using a reduced amount of black and white pixels.");
        row++;
        display.getInfoList().setText(row, 0, "Mono:");
        display.getInfoList().setText(row, 1, "Does only use black & white and six colors. Output looks more old-school.");
        row++;
        display.getInfoList().setText(row, 0, "LSD:");
        display.getInfoList().setText(row, 1, "Black and white are highly reduced in order to make the output far more colorful.");
        row++;
        display.getInfoList().setText(row, 0, "Skin:");
        display.getInfoList().setText(row, 1, "This scheme was optimized for images with skin. However, default mode conversion often look better.");
        row++;
        display.getInfoList().setText(row, 0, "Bwg:");
        display.getInfoList().setText(row, 1, "This scheme is only using black, white and two shades of gray. Works best for images without any color.");
        row++;
        display.getInfoList().setText(row, 0, "Bw:");
        display.getInfoList().setText(row, 1, "Only uses black and white. Works best for black & white images like stencils.");
        row++;
        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Contrast:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "The contrast is multiplied with the color of each pixel. Try to lower the contrast settings to improve the output for bright images.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "Brightness:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "The brightness is added to the color of each pixel. Try to adjust this value to fix the output after setting the contrast.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Kick:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "Sets an offset for methods that use anti aliasing. Setting another option than \"0\" may result in a better output. Changing the kick is particulary useful for images that have important regions, like for example the eyes in an image of a face.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "Binary Output for IRC:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        display.getInfoList().setText(row, 0, "Outputs the conversion as text with colors for IRC. You can copy and paste this into your IRC-client to forward your conversion to IRC. Most clients will show it with the exact same color as in the HTML-preview. The palette is obeying the defacto standard as used by mIRC. But beware, alot of IRC-servers don't allow flooding and may get you kicked for doing this. Depending on your IRC client you may consider use a script that pastes the output linewise with a delay of about 1500 ms in order to prevent this.");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0, "You can link to this application converting an image, by appending the image-url to the main link like this:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;
        final String externalLink = "http://upload.wikimedia.org/wikipedia/commons/0/03/RGB_Colorcube_Corner_White.png";
        final Hyperlink link = new Hyperlink("http://"
                + Window.Location.getHost() + "/Jenkem.html/#main/"
                + externalLink, "main/" + externalLink);
        display.getInfoList().setWidget(row, 0, link);
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        display.getInfoList().setWidget(row++, 0, new HTML("&nbsp;"));

        display.getInfoList().setText(row, 0,
                "The source code of this application can be found at:");
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
        row++;

        final String gitHubLink = "http://github.com/Tok/Jenkem-9000";
        display.getInfoList().setWidget(row, 0,
                new Anchor(gitHubLink, gitHubLink));
        display.getInfoList().getFlexCellFormatter().setColSpan(row, 0, 2);
    }
    //formatter:on

}
