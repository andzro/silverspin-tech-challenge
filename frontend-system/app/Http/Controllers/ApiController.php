<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;

class ApiController extends Controller
{
    private $client;
    private $orderApiBaseUrl;
    private $shippingApiBaseUrl;

    public function __construct()
    {
        $this->client = new Client();
        $this->orderApiBaseUrl = env('ORDER_API_URL', 'http://localhost:8080/api/orders'); // Set in .env
        $this->shippingApiBaseUrl = env('SHIPPING_API_URL', 'http://localhost:8081/api/shipments'); // Set in .env
    }

    /**
     * Get all orders
     */
    public function getOrders()
    {
        try {
            $response = $this->client->get("{$this->orderApiBaseUrl}");
            return response()->json(json_decode($response->getBody()), 200);
        } catch (RequestException $e) {
            return response()->json(['error' => 'Failed to fetch orders'], 500);
        }
    }

    /**
     * Create an order
     */
    public function createOrder(Request $request)
    {
        try {
            $response = $this->client->post("{$this->orderApiBaseUrl}", [
                'json' => [
                    'customerName' => $request->input('customer_name')
                ]
            ]);

            return response()->json(json_decode($response->getBody()), 201);
        } catch (RequestException $e) {
            return response()->json(['error' => 'Failed to create order'], 500);
        }
    }

    /**
     * Get all shipments
     */
    public function getShipments()
    {
        try {
            $response = $this->client->get("{$this->shippingApiBaseUrl}");
            return response()->json(json_decode($response->getBody()), 200);
        } catch (RequestException $e) {
            return response()->json(['error' => 'Failed to fetch shipments'], 500);
        }
    }

    /**
     * Create a shipment
     */
    public function createShipment(Request $request)
    {
        try {
            $response = $this->client->post("{$this->shippingApiBaseUrl}", [
                'json' => [
                    'orderId' => $request->input('orderId')
                ]
            ]);

            return response()->json(json_decode($response->getBody()), 201);
        } catch (RequestException $e) {
            return response()->json(['error' => 'Failed to create shipment'], 500);
        }
    }
}
