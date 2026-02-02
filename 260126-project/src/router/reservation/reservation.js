import Reservation from "@/views/reservation/Reservation.vue";
import DoctorSearch from "../../views/reservation/DoctorSearch.vue";
import DeptSearch from "../../views/reservation/DeptSearch.vue";
import CheckReservation from "../../views/reservation/CheckReservation.vue";

export default [
    {
        path: '/reservation',
        name: 'reservation',
        component: Reservation
    },
    {
        path: '/doctorsearch',
        name: 'doctorsearch',
        component: DoctorSearch
    },
    {
        path: '/deptsearch',
        name: 'deptsearch',
        component: DeptSearch
    },
    {
        path: '/checkreservation',
        name: 'checkreservation',
        component: CheckReservation
    }


]