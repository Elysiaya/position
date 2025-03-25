package com.example.position.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.position.ui.theme.PositionTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp

@Composable
fun DOYScreen(modifier: Modifier = Modifier){
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("")}
    var day by remember { mutableStateOf("") }
    var doy = 0
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center

    ){
        Text("DOY calculation", fontSize = 30.sp, modifier = Modifier.padding(bottom = 20.dp, top = 30.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically)
        {
//            Text("Year",modifier = Modifier.weight(0.2f), fontSize = 20.sp)
            TextField(value = year, onValueChange = {year = it},modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // 设置键盘类型为数字键盘
                ),label = { Text("year") })
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically)
        {
//            Text("Month",modifier = Modifier.weight(0.2f), fontSize = 18.sp)
            TextField(value = month, onValueChange = {month = it},modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // 设置键盘类型为数字键盘
                ),label = { Text("month") })
        }
        Spacer(modifier = Modifier.padding(5.dp))
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically)
        {
//            Text("Day",modifier = Modifier.weight(0.2f), fontSize = 20.sp)
            TextField(value = day, onValueChange = {day = it},modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number // 设置键盘类型为数字键盘
                ),label = { Text("day") })
        }
        Spacer(modifier = Modifier.padding(10.dp))

        Text("DOY:${dayOfYear(year = year.toIntOrNull(),month = month.toIntOrNull(),day = day.toIntOrNull()) ?: "Invalid date"}", fontSize = 30.sp)
    }
}
fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

fun dayOfYear(year: Int?, month: Int?, day: Int?): Int? {
    val daysInMonth = listOf(31, if (year?.let { isLeapYear(it) } == true) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

    if (month != null && year != null && day != null) {
        if (month < 1 || month > 12 || day < 1 || day > daysInMonth[month - 1]) {
//        throw IllegalArgumentException("Invalid date")
            return null
        }
        var dayOfYear = day
        for (i in 0 until month - 1) {
            dayOfYear += daysInMonth[i]
        }

        return dayOfYear
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun DOYScreenPreview() {
    PositionTheme {
        DOYScreen()
    }}