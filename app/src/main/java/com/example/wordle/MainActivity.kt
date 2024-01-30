package com.example.wordle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import layout.FourLetterWordList

class MainActivity : AppCompatActivity() {

    private lateinit var guessEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var resetButton: Button
    private lateinit var correctnessTextViews: List<TextView>
    private lateinit var correctAnswerTextView: TextView

    private val wordToGuess = FourLetterWordList.FourLetterWordList.getRandomFourLetterWord()
    private var remainingGuesses = 3
    private val correctnessList = mutableListOf<String>()
    private val guessesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guessEditText = findViewById(R.id.text_input)
        submitButton = findViewById(R.id.button)
        resetButton = findViewById(R.id.reset_button)
        correctnessTextViews = listOf(
            findViewById(R.id.correctivenessTextView6),
            findViewById(R.id.correctivenessTextView4),
            findViewById(R.id.correctivenessTextView8)
        )
        correctAnswerTextView = findViewById(R.id.correct_answer_textview)

        submitButton.setOnClickListener {
            handleGuess()
            hideKeyboard()
        }

        resetButton.setOnClickListener {
            resetGame()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleGuess() {
        val userGuess = guessEditText.text.toString().uppercase()
        val correctness = checkGuess(userGuess)

        // Store guesses and correctness information
        guessesList.add(userGuess)
        correctnessList.add(correctness)

        // Update correctness display for each TextView
        for (i in correctnessTextViews.indices) {
            val guessText = if (i < guessesList.size) "Guess ${i + 1}: ${guessesList[i]}" else ""
            val correctnessText = if (i < correctnessList.size) "Correctness: ${correctnessList[i]}" else ""

            correctnessTextViews[i].text = "$guessText\n$correctnessText"
        }

        // Decrease remaining guesses
        remainingGuesses--

        // Check if the user has exceeded the number of guesses
        if (remainingGuesses == 0) {
            // Disable the submit button
            submitButton.isEnabled = false

            // Show the reset button
            resetButton.visibility = View.VISIBLE

            // Display correct answer
            correctAnswerTextView.text = "Correct Answer: $wordToGuess"

            // Show a Toast to inform the user
            Toast.makeText(this, "You've exceeded your number of guesses!", Toast.LENGTH_SHORT).show()
        }

        // Clear the input field after handling the guess
        guessEditText.text.clear()
    }

    private fun checkGuess(guess: String): String {
        var result = ""
        for (i in guess.indices) {
            result += when {
                guess[i] == wordToGuess[i] -> "O"
                guess[i] in wordToGuess -> "+"
                else -> "X"
            }
        }
        return result
    }

    private fun resetGame() {
        remainingGuesses = 3
        guessesList.clear()
        correctnessList.clear()
        submitButton.isEnabled = true
        resetButton.visibility = View.GONE
        correctAnswerTextView.text = ""
        clearTextViews()
    }

    // Inside your activity or fragment
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(guessEditText.windowToken, 0)
    }

    // Inside your activity or fragment
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        currentFocus?.let {
            hideKeyboard()
        }
        return super.onTouchEvent(event)
    }

    private fun clearTextViews() {
        for (textView in correctnessTextViews) {
            textView.text = ""
        }
    }
}

