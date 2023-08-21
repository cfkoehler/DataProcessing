'use strict'

const AWS = require('aws-sdk')
const db = new AWS.DynamoDB.DocumentClient()
const TableName = "dp-MetadataIndex"

module.exports.getObject = (event, context, callback) => {
    const objectID = event.pathParameters.objectID
    console.info("Scanning for objectID: " + objectID)
    const params = {
        TableName,
        Key: {
            objectID: objectID
        }
    };
    db.scan(params, (error, result) => {
        if (error) {
            console.error(error)
            callback(null, {
                statusCode: error.statusCode || 501,
                headers: { 'Content-Type': 'text/plain' },
                body: 'Error fetching object with id: '
            })
            return
        }
        const response = {
            statusCode: 200,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(result.Items)
        }
        callback(null,response)
    })
}