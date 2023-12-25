package com.github.olson1998.http;

import org.apache.http.entity.ContentType;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class ReadOnlyHttpHeaders implements HttpHeaders {

    private final List<HttpHeader> readOnlyHeadersList;

    public ReadOnlyHttpHeaders(List<ReadOnlyHttpHeader> readOnlyHttpHeadersList) {
        this.readOnlyHeadersList = readOnlyHttpHeadersList.stream()
                .map(HttpHeader.class::cast)
                .toList();
    }

    @Override
    public List<HttpHeader> getHttpHeaderList() {
        return readOnlyHeadersList;
    }

    @Override
    public String getFirstValue(String httpHeader) {
        return findFirstValue(httpHeader).orElse(null);
    }

    @Override
    public Optional<ContentType> findContentType() {
        return findFirstValue(CONTENT_TYPE)
                .map(ContentType::parse);
    }

    @Override
    public Optional<String> findFirstValue(String httpHeader) {
        return readOnlyHeadersList.stream()
                .filter(header -> header.getKey().equals(httpHeader))
                .map(HttpHeader::getValue)
                .findFirst();
    }

    @Override
    public void appendHttpHeader(HttpHeader httpHeader) {

    }

    @Override
    public void appendHttpHeader(String httpHeader, String httpHeaderValue) {

    }

    @Override
    public int size() {
        return readOnlyHeadersList.size();
    }

    @Override
    public boolean isEmpty() {
        return readOnlyHeadersList.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return readOnlyHeadersList.stream()
                .anyMatch(httpHeader -> httpHeader.getKey().equals(String.valueOf(key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return readOnlyHeadersList.stream()
                .anyMatch(httpHeader -> httpHeader.getValue().equals(String.valueOf(value)));
    }

    @Override
    public List<String> get(Object key) {
        return readOnlyHeadersList.stream()
                .filter(httpHeader -> httpHeader.getKey().equals(String.valueOf(key)))
                .map(HttpHeader::getValue)
                .toList();
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return get(key);
    }

    @Override
    public List<String> remove(Object key) {
        return get(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<String> keySet() {
        return readOnlyHeadersList.stream()
                .map(HttpHeader::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<List<String>> values() {
        return entrySet().stream()
                .map(Map.Entry::getValue).toList();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        var headerEntries = new HashSet<Entry<String, List<String>>>();
        for(HttpHeader httpHeader : readOnlyHeadersList){
            var httpHeaderName = httpHeader.getKey();
            if(containsHeaderEntry(headerEntries, httpHeaderName)){
                headerEntries.stream()
                        .filter(headerEntry -> headerEntry.getKey().equals(httpHeaderName))
                        .findFirst()
                        .ifPresent(headerEntry -> headerEntry.getValue().add(httpHeader.getValue()));
            }else {
                var values = new ArrayList<String>();
                values.add(httpHeader.getValue());
                Entry<String, List<String>> entry = Map.entry(httpHeaderName, values);
                headerEntries.add(entry);
            }
        }
        return headerEntries.stream()
                .map(this::readOnlyHeader)
                .collect(Collectors.toUnmodifiableSet());
    }

    private boolean containsHeaderEntry(Set<Entry<String, List<String>>> headerEntriesSet, String headerName){
        return headerEntriesSet.stream().anyMatch(headerEntry -> headerEntry.getKey().equals(headerName));
    }

    private Entry<String, List<String>> readOnlyHeader(Entry<String, List<String>> headerEntry){
        return Map.entry(headerEntry.getKey(), headerEntry.getValue().stream().toList());
    }

    @Override
    public String toString() {
        return readOnlyHeadersList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
    }
}
