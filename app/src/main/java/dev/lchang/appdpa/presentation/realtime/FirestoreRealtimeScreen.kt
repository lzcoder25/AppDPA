package dev.lchang.appdpa.presentation.realtime

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

private val firestoreInstance =
        FirebaseFirestore.getInstance()
            .collection("demo")
            .document("test")


@Composable
fun FirestoreRealtimeScreen(){
    var contador by remember { mutableStateOf(0L) }
    var mensaje by remember { mutableStateOf("") }
    var mensajeUsuario by remember { mutableStateOf("") }
    var conectado by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = firestoreInstance.addSnapshotListener {
            snapshot, error ->
            if (error != null) {
                conectado = false
                // Handle error
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                contador = snapshot.getLong("contador") ?: 0L
                mensaje = snapshot.getString("mensaje") ?: ""
            }
            conectado = true
        }
        onDispose {
            // Remove the listener when the composable is disposed
            listener.remove()
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Firestore Realtime", style = MaterialTheme.typography.titleLarge
        )
        Text(
            if (conectado) "Conectado" else "Conectando...",
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = {
                firestoreInstance
                    .set(mapOf("valor"
                            to FieldValue.increment(1)),
                        SetOptions.merge())

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+1")
        }

        //Mostrar el mensaje del documento en tiempo real
        Text("Mensaje actual: $mensaje")

        OutlinedTextField(
             value = mensajeUsuario,
             onValueChange = {mensajeUsuario = it},
             label = {Text("Mensaje")},
             placeholder = {Text("Mensaje")},
             modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                firestoreInstance.set(mapOf("mensaje" to mensajeUsuario),
                    SetOptions.merge())
                mensajeUsuario = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar mensaje")
        }
    }

}


