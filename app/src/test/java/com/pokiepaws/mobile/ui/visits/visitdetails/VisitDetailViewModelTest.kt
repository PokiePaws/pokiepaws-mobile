package com.pokiepaws.mobile.ui.visits.visitdetails

import com.pokiepaws.mobile.MainDispatcherRule
import com.pokiepaws.mobile.domain.connectivity.ConnectivityObserver
import com.pokiepaws.mobile.domain.model.Clinic
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.domain.repository.ClinicRepository
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

class VisitDetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val visitRepository = mockk<VisitRepository>()
    private val clinicRepository = mockk<ClinicRepository>()
    private val connectivityObserver = FakeConnectivityObserver()

    @Test
    fun cancelVisitCallsRepositoryAndInvokesDoneCallbackWhenOnline() =
        runTest {
            coEvery { visitRepository.cancel(visitId = 15L) } returns sampleVisit(id = 15L)
            val viewModel = createViewModel()
            var doneCalled = false

            viewModel.cancel(15L) {
                doneCalled = true
            }

            assertTrue(doneCalled)
            coVerify { visitRepository.cancel(15L) }
        }

    @Test
    fun cancelVisitIsBlockedWhenOffline() =
        runTest {
            connectivityObserver.isOnline.value = false
            val viewModel = createViewModel()

            viewModel.cancel(15L) {
                error("Callback should not be called while offline")
            }

            val state = viewModel.uiState.value as VisitDetailUiState.Error
            assertEquals("Ta akcja wymaga połączenia z internetem", state.message)
            coVerify(exactly = 0) { visitRepository.cancel(any()) }
        }

    @Test
    fun loadCombinesVisitClinicAndPrescription() =
        runTest {
            val visit = sampleVisit(id = 15L, clinicId = 2L)
            val clinic = sampleClinic(id = 2L)
            val prescription = samplePrescription(visitId = 15L)
            every { visitRepository.getById(15L) } returns flowOf(visit)
            every { clinicRepository.getClinics() } returns flowOf(listOf(clinic))
            every { visitRepository.observePrescription(15L) } returns flowOf(prescription)
            coEvery { visitRepository.syncById(15L) } returns Unit
            coEvery { clinicRepository.syncClinics() } returns Unit
            coEvery { visitRepository.syncPrescription(15L) } returns Unit
            val viewModel = createViewModel()

            viewModel.load(15L)

            val state = viewModel.uiState.value as VisitDetailUiState.Success
            assertEquals(visit, state.visit)
            assertEquals(clinic, state.clinic)
            assertEquals(prescription, state.prescription)
        }

    private fun createViewModel() =
        VisitDetailViewModel(
            repo = visitRepository,
            clinicRepository = clinicRepository,
            connectivityObserver = connectivityObserver,
        )
}

private class FakeConnectivityObserver(
    override val isOnline: MutableStateFlow<Boolean> = MutableStateFlow(true),
) : ConnectivityObserver

private fun sampleVisit(
    id: Long,
    clinicId: Long = 10L,
) = Visit(
    id = id,
    animalId = 44L,
    clinicId = clinicId,
    vetUserId = 20L,
    startsAt = "2026-07-01T10:00:00",
    endsAt = "2026-07-01T10:30:00",
    description = VisitDescription.CHECKUP,
    disease = "Kontrola",
    diagnosis = "Zdrowy",
    recommendations = "Obserwacja",
    status = "SCHEDULED",
)

private fun sampleClinic(id: Long) =
    Clinic(
        id = id,
        clinicName = "Pokie Vet",
        city = "Legnica",
        street = "Zielona",
        houseNumber = "1",
        postalCode = "59-220",
        country = "Poland",
    )

private fun samplePrescription(visitId: Long) =
    Prescription(
        id = 1L,
        visitId = visitId,
        vetUserId = 20L,
        clinicId = 10L,
        recommendationDate = "2026-07-01",
        creationDate = "2026-07-01",
        items = emptyList(),
    )
