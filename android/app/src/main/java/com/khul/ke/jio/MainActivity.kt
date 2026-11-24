package com.khul.ke.jio

import android.os.Bundle
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etDob = findViewById<EditText>(R.id.et_dob)
        val btnCalculate = findViewById<MaterialButton>(R.id.btn_calculate)
        val tvResult = findViewById<MaterialTextView>(R.id.tv_result)
        val llChecklist = findViewById<LinearLayout>(R.id.ll_checklist)
        val tvChecklistTitle = findViewById<MaterialTextView>(R.id.tv_checklist_title)
        val llChecklistItems = findViewById<LinearLayout>(R.id.ll_checklist_items)

        btnCalculate.setOnClickListener {
            val dobStr = etDob.text.toString().trim()
            if (dobStr.isEmpty()) {
                tvResult.text = "Please enter your date of birth"
                tvResult.setTextColor(resources.getColor(R.color.red))
                llChecklist.visibility = View.GONE
                return
            }

            try {
                val dob = LocalDate.parse(dobStr)
                val today = LocalDate.now()

                // Validate: no future dates
                if (dob.isAfter(today)) {
                    tvResult.text = "Date of birth cannot be a future date"
                    tvResult.setTextColor(resources.getColor(R.color.red))
                    llChecklist.visibility = View.GONE
                    return
                }

                val period = Period.between(dob, today)
                val ageYears = period.years
                val ageMonths = period.months
                val ageDays = period.days

                // Format age display
                val ageText = when {
                    ageYears > 0 -> "$ageYears years, $ageMonths months, $ageDays days"
                    ageMonths > 0 -> "$ageMonths months, $ageDays days"
                    else -> "$ageDays days old"
                }

                tvResult.text = "Age: $ageText"
                tvResult.setTextColor(resources.getColor(R.color.green))

                // Determine eligibility
                val isAdult = ageYears >= 18
                val isMinor = ageYears < 18

                // Show/hide checklist based on age
                llChecklist.visibility = View.VISIBLE

                if (isAdult) {
                    tvChecklistTitle.text = "Document Eligibility Checklist"
                    populateAdultChecklist(llChecklistItems, isAdult)
                } else {
                    tvChecklistTitle.text = "Minor Guidance"
                    populateMinorChecklist(llChecklistItems, isMinor)
                }

            } catch (e: Exception) {
                tvResult.text = "Invalid date format. Use YYYY-MM-DD"
                tvResult.setTextColor(resources.getColor(R.color.red))
                llChecklist.visibility = View.GONE
            }
        }
    }

    private fun populateAdultChecklist(llChecklistItems: LinearLayout, isAdult: Boolean) {
        llChecklistItems.removeAllViews()

        val items = arrayOf(
            DocumentItem(R.drawable.ic_aadhaar, "Aadhaar Card", "Available to all citizens (above 18)", isAdult),
            DocumentItem(R.drawable.ic_pan, "PAN Card", "Available to all citizens (above 18)", isAdult),
            DocumentItem(R.drawable.ic_voter_id, "Voter ID Card", "Available to all citizens (above 18)", isAdult),
            DocumentItem(R.drawable.ic_passport, "Passport", "Available to all citizens (above 18)", isAdult),
            DocumentItem(R.drawable.ic_driving_licence, "Driving Licence", "Available at age 18+", isAdult),
            DocumentItem(R.drawable.ic_bank_account, "Bank Account", "Available to all citizens (above 18)", isAdult),
            DocumentItem(R.drawable.ic_atm_debit_card, "ATM/Debit Card", "Available at age 18+", isAdult)
        )

        items.forEach { item ->
            val view = layoutInflater.inflate(R.layout.checklist_item, llChecklistItems, false)
            view.findViewById<ImageView>(R.id.ic_document).setImageResource(item.resId)
            view.findViewById<MaterialTextView>(R.id.tv_document_name).text = item.name
            view.findViewById<MaterialTextView>(R.id.tv_document_desc).text = item.description
            // Set visibility based on eligibility
            view.findViewById<View>(R.id.v_eligible).visibility = if (item.eligible) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.v_minor_only).visibility = if (!item.eligible && item.minorOnly) View.VISIBLE else View.GONE
            llChecklistItems.addView(view)
        }
    }

    private fun populateMinorChecklist(llChecklistItems: LinearLayout, isMinor: Boolean) {
        llChecklistItems.removeAllViews()

        // For minors, show only documents available below 18
        val minorItems = arrayOf(
            DocumentItem(R.drawable.ic_aadhaar, "Aadhaar Card", "Available to all citizens including minors", true),
            DocumentItem(R.drawable.ic_pan, "PAN Card", "Available to minors (above 10 years)", true),
            DocumentItem(R.drawable.ic_driving_licence, "Learner's Licence", "Available at age 16+ with guardian consent", false),
            DocumentItem(R.drawable.ic_bank_account, "Minor Bank Account", "Available with guardian, operations limited", true)
        )

        minorItems.forEach { item ->
            val view = layoutInflater.inflate(R.layout.checklist_item, llChecklistItems, false)
            view.findViewById<ImageView>(R.id.ic_document).setImageResource(item.resId)
            view.findViewById<MaterialTextView>(R.id.tv_document_name).text = item.name
            view.findViewById<MaterialTextView>(R.id.tv_document_desc).text = item.description
            view.findViewById<View>(R.id.v_eligible).visibility = if (item.eligible) View.VISIBLE else View.GONE
            view.findViewById<View>(R.id.v_minor_only).visibility = if (!item.eligible && item.minorOnly) View.VISIBLE else View.GONE
            llChecklistItems.addView(view)
        }

        // Show the motivational message
        val tvMotivational = MaterialTextView(this).apply {
            text = "KHUL KE JIO ❤️\nAbhi apni padhai, skills aur sapno par focus karo!"
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 18
            setTextColor(resources.getColor(R.color.primary))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 20, 0, 20)
            llChecklistItems.addView(this)
        }
    }

    data class DocumentItem(
        val resId: Int,
        val name: String,
        val description: String,
        val eligible: Boolean,
        val minorOnly: Boolean = false
    )
}