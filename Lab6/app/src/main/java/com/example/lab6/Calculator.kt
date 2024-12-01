package com.example.Lab6

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.ceil

@Stable
data class ElectricParams(
    var name: String = "",
    var efficiency: String = "",
    var powerFactor: String = "",
    var voltage: String = "",
    var quantity: String = "",
    var ratedPower: String = "",
    var usageCoefficient: String = "",
    var reactivePowerCoefficient: String = "",
    var powerProduct: String = "",
    var current: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Calculator() {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var equipmentList by remember { mutableStateOf(listOf(
        ElectricParams(
            name = "Шліфувальний верстат",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "4",
            ratedPower = "22",
            usageCoefficient = "0.33",
            reactivePowerCoefficient = "1.33"
        ),
        ElectricParams(
            name = "Свердлильний верстат",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "2",
            ratedPower = "14",
            usageCoefficient = "0.12",
            reactivePowerCoefficient = "1"
        ),
        ElectricParams(
            name = "Фугувальний верстат",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "4",
            ratedPower = "42",
            usageCoefficient = "0.15",
            reactivePowerCoefficient = "1.33"
        ),
        ElectricParams(
            name = "Циркулярна пила",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "1",
            ratedPower = "36",
            usageCoefficient = "0.3",
            reactivePowerCoefficient = "1.57"
        ),
        ElectricParams(
            name = "Прес",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "1",
            ratedPower = "20",
            usageCoefficient = "0.5",
            reactivePowerCoefficient = "0.75"
        ),
        ElectricParams(
            name = "Полірувальний верстат",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "1",
            ratedPower = "40",
            usageCoefficient = "0.23",
            reactivePowerCoefficient = "1"
        ),
        ElectricParams(
            name = "Фрезерний верстат",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "2",
            ratedPower = "32",
            usageCoefficient = "0.2",
            reactivePowerCoefficient = "1"
        ),
        ElectricParams(
            name = "Вентилятор",
            efficiency = "0.92",
            powerFactor = "0.9",
            voltage = "0.38",
            quantity = "1",
            ratedPower = "20",
            usageCoefficient = "0.65",
            reactivePowerCoefficient = "0.75"
        ),
    )) }

    var Kr by remember { mutableStateOf("1.25") }
    var Kr2 by remember { mutableStateOf("0.7") }

    var sumOfPowerProduct by remember { mutableStateOf(0.0) }

    var groupUsageCoefficient by remember { mutableStateOf("") }
    var effectiveEquipmentAmount by remember { mutableStateOf("") }

    var totalDepartmentUsageCoefficient by remember { mutableStateOf("") }
    var effectiveDepartmentEquipmentAmount by remember { mutableStateOf("") }
    var activeLoadCalculation by remember { mutableStateOf("") }
    var reactiveLoadCalculation by remember { mutableStateOf("") }
    var fullPower by remember { mutableStateOf("") }
    var groupCurrentCalculationShr1 by remember { mutableStateOf("") }

    var activeLoadCalculationBus by remember { mutableStateOf("") }
    var reactiveLoadCalculationBus by remember { mutableStateOf("") }
    var fullPowerBus by remember { mutableStateOf("") }
    var groupCurrentCalculationBus by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(scrollState)
    ) {
        Button(
            onClick = { equipmentList = equipmentList + ElectricParams() },
            modifier = Modifier.padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Додати обладнання")
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(equipmentList) { params ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    ElectricParamsForm(params = params)
                }
            }
        }

        Button(
            onClick = {
                var sumOfProduct = 0.0
                var sumOfPower = 0.0
                var sumOfPowerSquare = 0.0

                equipmentList.forEach { params ->
                    val n = params.quantity.toDouble()
                    val Pn = params.ratedPower.toDouble()
                    params.powerProduct = "${n * Pn}"
                    val Ip = params.powerProduct.toDouble() / (sqrt(3.0) * params.voltage.toDouble() * params.powerFactor.toDouble() * params.efficiency.toDouble())
                    params.current = Ip.toString()

                    sumOfProduct += params.powerProduct.toDouble() * params.usageCoefficient.toDouble()
                    sumOfPower += params.powerProduct.toDouble()
                    sumOfPowerSquare += params.quantity.toDouble() * params.ratedPower.toDouble() * params.ratedPower.toDouble()
                }

                sumOfPowerProduct = sumOfProduct

                val groupUtilCoefficient = sumOfProduct / sumOfPower
                val effectiveEquipment = ceil((sumOfPower * sumOfPower) / sumOfPowerSquare)

                groupUsageCoefficient = groupUtilCoefficient.toString()
                effectiveEquipmentAmount = effectiveEquipment.toString()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Обчислити")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Коефіцієнт використання групи: $groupUsageCoefficient",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ефективна кількість обладнання: $effectiveEquipmentAmount",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = Kr,
            onValueChange = { Kr = it },
            label = { Text("Розрахунковий коеф активної потужності") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusManager.clearFocus() }
        )

        Button(
            onClick = {
                val KvValue = groupUsageCoefficient.toDouble()
                val KrValue = Kr.toDouble()
                val PH = 22.0 // за варіантом 3
                val tan_phi = 1.57 // за варіантом 3
                val Un = 0.38

                val Pp = KrValue * sumOfPowerProduct
                val Qp = KvValue * PH * tan_phi
                val Sp = sqrt((Pp * Pp) + (Qp * Qp))
                val Ip = Pp / Un

                val KvDepartment = 752.0 / 2330.0
                val n_e = 2330.0 * 2330.0 / 96399.0

                activeLoadCalculation = Pp.toString()
                reactiveLoadCalculation = Qp.toString()
                fullPower = Sp.toString()
                groupCurrentCalculationShr1 = Ip.toString()
                totalDepartmentUsageCoefficient = KvDepartment.toString()
                effectiveDepartmentEquipmentAmount = n_e.toString()

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Обчислити")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Розрахункове активне навантаження: $activeLoadCalculation",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Розрахункове реактивне навантаження: $reactiveLoadCalculation",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Повна потужність: $fullPower",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Розрахунковий груповий струм ШР1: $groupCurrentCalculationShr1",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Коефіцієнт використання цеху в цілому: $totalDepartmentUsageCoefficient",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ефективна кількість обладнання цеху в цілому: $effectiveDepartmentEquipmentAmount",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = Kr2,
            onValueChange = { Kr2 = it },
            label = { Text("Розрахунковий коеф активної потужності") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { focusManager.clearFocus() }
        )

        Button(
            onClick = {
                val KvValue = Kr2.toDouble()

                val Pp = KvValue * 752.0
                val Qp = KvValue * 657.0
                val Sp = sqrt((Pp * Pp) + (Qp * Qp))
                val Ip = Pp / 0.38

                activeLoadCalculationBus = Pp.toString()
                reactiveLoadCalculationBus = Qp.toString()
                fullPowerBus = Sp.toString()
                groupCurrentCalculationBus = Ip.toString()

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Обчислити")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Розрахункове активне навантаження на шинах 0,38 кВ ТП: $activeLoadCalculationBus",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Розрахункове реактивне навантаження на шинах 0,38 кВ ТП: $reactiveLoadCalculationBus",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Повна потужність на шинах 0,38 кВ ТП: $fullPowerBus",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Розрахунковий груповий струм на шинах 0,38 кВ ТП: $groupCurrentCalculationBus",
            style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricParamsForm(params: ElectricParams) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = params.name,
        onValueChange = { params.name = it },
        label = { Text("Назва обладнання") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.efficiency,
        onValueChange = { params.efficiency = it },
        label = { Text("ККД (ηн)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.powerFactor,
        onValueChange = { params.powerFactor = it },
        label = { Text("Коефіцієнт потужності (cos φ)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.voltage,
        onValueChange = { params.voltage = it },
        label = { Text("Напруга (Uн, кВ)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.quantity,
        onValueChange = { params.quantity = it },
        label = { Text("Кількість (n, шт)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.ratedPower,
        onValueChange = { params.ratedPower = it },
        label = { Text("Номінальна потужність (Pн, кВт)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.usageCoefficient,
        onValueChange = { params.usageCoefficient = it },
        label = { Text("Коефіцієнт використання (КВ)") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
    OutlinedTextField(
        value = params.reactivePowerCoefficient,
        onValueChange = { params.reactivePowerCoefficient = it },
        label = { Text("tg φ") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
    )
}