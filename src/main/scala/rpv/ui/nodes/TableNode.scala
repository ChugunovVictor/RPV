package rpv.ui.nodes

import rpv.helpers.Сoncealer
import scalafx.scene.control.cell.TextFieldTableCell
import scalafx.scene.control.{Button, TableCell, TableColumn, TableView}
import scalafx.scene.effect.DropShadow
import scalafx.util.converter.{DoubleStringConverter, IntStringConverter}

class TableNode(removeConcealer: => String => Unit) extends TableView[Сoncealer] {
  editable = true
  columns ++= List(
    new TableColumn[Сoncealer, Double] {
      text = "X"
      cellValueFactory = {
        _.value.x
      }
      cellFactory = (_: TableColumn[Сoncealer, Double]) =>
        new TextFieldTableCell[Сoncealer, Double](new DoubleStringConverter())
    },
    new TableColumn[Сoncealer, Double] {
      text = "Y"
      cellValueFactory = {
        _.value.y
      }
      cellFactory = (_: TableColumn[Сoncealer, Double]) =>
        new TextFieldTableCell[Сoncealer, Double](new DoubleStringConverter())
    },
    new TableColumn[Сoncealer, Double] {
      text = "Width"
      cellValueFactory = {
        _.value.width
      }
      cellFactory = (_: TableColumn[Сoncealer, Double]) =>
        new TextFieldTableCell[Сoncealer, Double](new DoubleStringConverter())
    },
    new TableColumn[Сoncealer, Double] {
      text = "Height"
      cellValueFactory = {
        _.value.height
      }
      cellFactory = (_: TableColumn[Сoncealer, Double]) =>
        new TextFieldTableCell[Сoncealer, Double](new DoubleStringConverter())
    },
    new TableColumn[Сoncealer, Int] {
      text = "From"
      cellValueFactory = {
        _.value.from
      }
      cellFactory = (_: TableColumn[Сoncealer, Int]) =>
        new TextFieldTableCell[Сoncealer, Int](new IntStringConverter())
    },
    new TableColumn[Сoncealer, Int] {
      text = "To"
      cellValueFactory = {
        _.value.to
      }
      cellFactory = (_: TableColumn[Сoncealer, Int]) =>
        new TextFieldTableCell[Сoncealer, Int](new IntStringConverter())
    },
    new TableColumn[Сoncealer, Unit] {
      prefWidth = 20
      minWidth = 20
      maxWidth = 20
      text = ""
      cellValueFactory = {
        _.value.action
      }
      cellFactory = { _: TableColumn[Сoncealer, Unit] =>
        new TableCell[Сoncealer, Unit] {
          // style="-fx-alignment: CENTER"

          text = ""
          item.onChange { (_, _, value) =>
            graphic = if (value != null) new Button {
              text = ""
              style =
                """   -icon-paint: red;
                  |       -fx-background-color: -icon-paint;
                  |   -size: 12;
                  |       -fx-min-height: -size;
                  |       -fx-min-width:  -size;
                  |       -fx-max-height: -size;
                  |       -fx-max-width:  -size;
                  |   -fx-shape: "M 50 0 L 0 50 450 500 500 450 Z M 450 0 L 0 450 50 500 500 50 Z";
                  |""".stripMargin
              onMouseEntered = e => this.effect = new DropShadow()
              onMouseExited = e => this.effect = null
              onMouseClicked = e => {
                val i = tableRow.value.getItem
                removeConcealer(i.id.value)
                items.value.remove(i)
              }
            } else null
          }
        }
      }
    }
  )

  columnResizePolicy = TableView.ConstrainedResizePolicy
}
