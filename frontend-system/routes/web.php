<?php

use Illuminate\Support\Facades\Route;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use App\Http\Controllers\ApiController;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Auth::routes();

Route::get('/home', [App\Http\Controllers\HomeController::class, 'index'])->name('home');

Route::get('/dashboard', function () {
    return view('dashboard');
})->middleware(['auth'])->name('dashboard');

Route::get('/orders', [ApiController::class, 'getOrders'])->name('orders.index');
Route::post('/orders', [ApiController::class, 'createOrder'])->name('orders.create');

Route::get('/shipments', [ApiController::class, 'getShipments'])->name('shipments.index');
Route::post('/shipments', [ApiController::class, 'createShipment'])->name('shipments.create');

