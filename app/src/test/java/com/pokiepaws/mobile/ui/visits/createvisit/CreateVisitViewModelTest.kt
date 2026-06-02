package com.pokiepaws.mobile.ui.visits.createvisit

import app.cash.turbine.test
import com.pokiepaws.mobile.MainDispatcherRule
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.CreateVisitDraft
import com.pokiepaws.mobile.domain.model.CreateVisitStep
import com.pokiepaws.mobile.domain.model.Vet
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.domain.repository.ClinicRepository
import com.pokiepaws.mobile.domain.repository.VetRepository
import com.pokiepaws.mobile.domain.repository.VisitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class CreateVisitViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clinicRepository = mockk<ClinicRepository>()
    private val vetRepository = mockk<VetRepository>()
    private val visitRepository = mockk<VisitRepository>()
    private val connectivityObserver = FakeConnectivityObserver()

    @Test
    fun selectingClinicVetDateAndSlotBuildsCreateVisitDraftOnConfirm() =
        runTest {
            val clinic = sampleClinic()
            val vet = sampleVet()
            val date = LocalDate.now().plusDays(1).toString()
            val slot = "${date}T10:00:00"

            stubInitialData(clinic)
            every { vetRepository.getByClinic(clinic.id) } returns flowOf(listOf(vet))
            coEvery { vetRepository.syncByClinic(clinic.id) } returns Unit
            coEvery { visitRepository.getAvailableSlots(clinic.id, vet.userId, date) } returns listOf(slot)
            coEvery { visitRepository.create(any()) } returns sampleVisit(slot = slot)

            val viewModel = createViewModel()

            viewModel.uiState.test {
                viewModel.selectClinic(clinic)
                viewModel.selectVet(vet)
                viewModel.selectDate(date)
                viewModel.selectSlot(slot)
                viewModel.updateDescription(VisitDescription.VACCINATION)
                viewModel.confirm(animalId = 44L)

                val successState = expectMostRecentItem()
                assertTrue(successState.success)
                assertEquals(CreateVisitStep.CONFIRM, successState.step)

                coVerify {
                    visitRepository.create(
                        CreateVisitDraft(
                            animalId = 44L,
                            clinicId = clinic.id,
                            vetUserId = vet.userId,
                            startsAt = slot,
                            description = VisitDescription.VACCINATION,
                        ),
                    )
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun dateSelectionIsBlockedWhenOffline() =
        runTest {
            val clinic = sampleClinic()
            val vet = sampleVet()
            val date = LocalDate.now().plusDays(1).toString()

            connectivityObserver.isOnline.value = false
            stubInitialData(clinic)
            every { vetRepository.getByClinic(clinic.id) } returns flowOf(listOf(vet))
            coEvery { vetRepository.syncByClinic(clinic.id) } returns Unit

            val viewModel = createViewModel()
            viewModel.selectClinic(clinic)
            viewModel.selectVet(vet)
            viewModel.selectDate(date)

            val state = viewModel.uiState.value
            assertTrue(state.error.orEmpty().contains("internetem"))
            coVerify(exactly = 0) {
                visitRepository.getAvailableSlots(any(), any(), any())
            }
        }

    private fun createViewModel() =
        CreateVisitViewModel(
            clinicRepository = clinicRepository,
            vetRepository = vetRepository,
            visitRepository = visitRepository,
            connectivityObserver = connectivityObserver,
        )

    private fun stubInitialData(clinic: Clinic) {
        every { clinicRepository.getClinics() } returns flowOf(listOf(clinic))
        coEvery { clinicRepository.syncClinics() } returns Unit
    }
}

private class FakeConnectivityObserver(
    override val isOnline: MutableStateFlow<Boolean> = MutableStateFlow(true),
) : ConnectivityObserver

private fun sampleClinic() =
    Clinic(
        id = 10L,
        clinicName = "Pokie Vet",
        city = "Legnica",
        street = "Zielona",
        houseNumber = "1",
        postalCode = "59-220",
        country = "Poland",
    )

private fun sampleVet() =
    Vet(
        userId = 20L,
        firstName = "Anna",
        lastName = "Nowak",
    )

private fun sampleVisit(
    id: Long = 99L,
    slot: String = "2026-07-01T10:00:00",
) = Visit(
    id = id,
    animalId = 44L,
    clinicId = 10L,
    vetUserId = 20L,
    startsAt = slot,
    endsAt = slot,
    description = VisitDescription.VACCINATION,
    disease = null,
    diagnosis = null,
    recommendations = null,
    status = "SCHEDULED",
)
