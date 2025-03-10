@extends('layouts.app')

@section('content')
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <div class="card">
                <div class="card-header">{{ __('Dashboard') }}</div>

                <div class="card-body">
                    @if (session('status'))
                        <div class="alert alert-success" role="alert">
                            {{ session('status') }}
                        </div>
                    @endif

                    <h4>Create Order</h4>
                    <form action="{{ route('orders.create') }}" method="POST">
                        @csrf
                        <div class="mb-3">
                            <label for="customer_name" class="form-label">Customer Name</label>
                            <input type="text" class="form-control" name="customer_name" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Create Order</button>
                    </form>

                    <hr>

                    <h4>Create Shipment</h4>
                    <form action="{{ route('shipments.create') }}" method="POST">
                        @csrf
                        <div class="mb-3">
                            <label for="order_id" class="form-label">Order ID</label>
                            <input type="number" class="form-control" name="order_id" required>
                        </div>
                        <button type="submit" class="btn btn-success">Create Shipment</button>
                    </form>

                    <hr>

                    <h4>Fetch Orders</h4>
                    <button class="btn btn-info" onclick="fetchOrders()">Get Orders</button>
                    <ul id="orders-list" class="list-group mt-3"></ul>

                    <hr>

                    <h4>Fetch Shipments</h4>
                    <button class="btn btn-warning" onclick="fetchShipments()">Get Shipments</button>
                    <ul id="shipments-list" class="list-group mt-3"></ul>

                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function fetchOrders() {
        fetch("{{ route('orders.index') }}")
            .then(response => response.json())
            .then(data => {
                let ordersList = document.getElementById('orders-list');
                ordersList.innerHTML = '';
                data.content.forEach(order => {
                    let listItem = document.createElement('li');
                    listItem.textContent = `Order ID: ${order.id}, Customer: ${order.customerName}, Status: ${order.status}`;
                    listItem.classList.add('list-group-item');
                    ordersList.appendChild(listItem);
                });
            });
    }

    function fetchShipments() {
        fetch("{{ route('shipments.index') }}")
            .then(response => response.json())
            .then(data => {
                let shipmentsList = document.getElementById('shipments-list');
                shipmentsList.innerHTML = '';
                data.content.forEach(shipment => {
                    let listItem = document.createElement('li');
                    listItem.textContent = `Shipment ID: ${shipment.id}, Order ID: ${shipment.orderId}, Status: ${shipment.status}`;
                    listItem.classList.add('list-group-item');
                    shipmentsList.appendChild(listItem);
                });
            });
    }
</script>

@endsection
