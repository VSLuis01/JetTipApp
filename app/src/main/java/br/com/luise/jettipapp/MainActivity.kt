package br.com.luise.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.luise.jettipapp.components.InputField
import br.com.luise.jettipapp.ui.theme.JetTipAppTheme
import br.com.luise.jettipapp.utils.calculateTotalPerPerson
import br.com.luise.jettipapp.utils.calculateTotalTip
import br.com.luise.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                MyApp {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.padding(all = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        content()
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(all = 10.dp)
            .clip(CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7),
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total por pessoa",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            val total = "%.2f".format(totalPerPerson)

            Text(
                text = "R$$total",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
        }
    }
}


@Preview
@Composable
fun MainContent() {
    val splitByState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    Column(modifier = Modifier.padding(all = 12.dp)) {
        TopHeader(totalPerPerson = totalPerPersonState.value)

        BillForm(
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        ){}
    }


}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValChanged: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = remember {
        mutableIntStateOf(0)
    }

    Surface(
        modifier = modifier
            .padding(all = 2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = modifier.padding(all = 6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Entre com o valor da conta",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    keyboardController?.hide()
                }
            )

            if (validState) {
                //Buttons Row
                Row(modifier = modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Dividir",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(150.dp))

                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            if (splitByState.value > 1) {
                                splitByState.value -= 1
                            }

                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage.value
                            )
                        })

                        Text(
                            text = "${splitByState.value}",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            splitByState.value += 1

                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage.value
                            )
                        })
                    }
                }

                // Tip Row
                Row(modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        text = "Gorjeta"
                    )

                    Spacer(modifier = Modifier.width(180.dp))

                    Text(
                        modifier = Modifier.align(alignment = Alignment.CenterVertically),
                        text = "R$${tipAmountState.value}"
                    )
                }

                //Slider Column
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${tipPercentage.value}%")

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider
                    Slider(
                        value = sliderPositionState.value,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 0,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal

                            tipPercentage.value = (sliderPositionState.value * 100).toInt()

                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage.value
                                )

                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage.value
                            )
                        })
                }
            } else {
                Box {}
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipAppTheme {
        MyApp {
            Text(text = "Hello AGAIN")
        }
    }
}