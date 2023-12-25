package com.github.olson1998.http;

import lombok.Getter;
import org.apache.http.entity.ContentType;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;

public class Headers implements HttpHeaders {

    private final ArrayList<HttpHeader> httpHeaderList;

    public Headers() {
        this.httpHeaderList = new ArrayList<>();
    }

    public Headers(List<HttpHeader> httpHeaders) {
        this.httpHeaderList = new ArrayList<>();
        for(HttpHeader httpHeader : httpHeaders){
            appendHttpHeader(httpHeaderList, httpHeader);
        }
    }

    @Override
    public List<HttpHeader> getHttpHeaderList() {
        return httpHeaderList;
    }

    @Override
    public String getFirstValue(String httpHeader) {
        return findFirstValue(httpHeader)
                .orElse(null);
    }

    @Override
    public Optional<ContentType> findContentType() {
        return httpHeaderList.stream()
                .filter(header -> header.getKey().equals(CONTENT_TYPE))
                .map(HttpHeader::getValue)
                .map(ContentType::parse)
                .findFirst();
    }

    @Override
    public Optional<String> findFirstValue(String httpHeader) {
        return httpHeaderList.stream()
                .filter(header -> header.getKey().equals(httpHeader))
                .findFirst()
                .map(HttpHeader::getValue);
    }

    @Override
    public void appendHttpHeader(HttpHeader httpHeader) {
        if(httpHeaderList.stream().noneMatch(header -> header.getKey().equals(httpHeader.getKey()) && header.getValue().equals(httpHeader.getValue()))){
            httpHeaderList.add(httpHeader);
        }
    }

    @Override
    public void appendHttpHeader(String httpHeader, String httpHeaderValue) {
        appendHttpHeader(new Header(httpHeader, httpHeaderValue));
    }

    @Override
    public int size() {
        return httpHeaderList.size();
    }

    @Override
    public boolean isEmpty() {
        return httpHeaderList.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return httpHeaderList.stream()
                .map(HttpHeader::getKey)
                .anyMatch(httpHeader -> httpHeader.equals(String.valueOf(key)));
    }

    @Override
    public boolean containsValue(Object value) {
        return httpHeaderList.stream()
                .map(HttpHeader::getValue)
                .anyMatch(headerValue -> headerValue.equals(String.valueOf(value)));
    }

    @Override
    public List<String> get(Object key) {
        return httpHeaderList.stream()
                .filter(httpHeader -> httpHeader.getKey().equals(String.valueOf(key)))
                .map(HttpHeader::getValue)
                .toList();
    }

    @Override
    public List<String> put(String key, List<String> value) {
        var headers = value.stream()
                .map(HttpHeader::of)
                .toList();
        this.httpHeaderList.addAll(headers);
        return httpHeaderList.stream()
                .map(HttpHeader::getValue)
                .toList();
    }

    @Override
    public List<String> remove(Object key) {
        var headers = httpHeaderList.stream()
                .filter(httpHeader -> httpHeader.getKey().equals(String.valueOf(key)))
                .toList();
        httpHeaderList.removeAll(headers);
        return headers.stream()
                .map(HttpHeader::getValue)
                .toList();
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        m.forEach((header, values) -> values.forEach(value ->{
            if(httpHeaderList.stream().noneMatch(httpHeader -> httpHeader.getKey().equals(header))){
                var httpHeader = new Header(header, value);
                httpHeaderList.add(httpHeader);
            }
        }));
    }

    @Override
    public void clear() {
        httpHeaderList.clear();
    }

    @Override
    public Set<String> keySet() {
        return httpHeaderList.stream()
                .map(HttpHeader::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<List<String>> values() {
        return this.entrySet().stream()
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        var headerEntries = new HashSet<Entry<String, List<String>>>();
        for(HttpHeader httpHeader : httpHeaderList){
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

    private void appendHttpHeader(List<HttpHeader> httpHeaderList, HttpHeader httpHeader){
        var name = httpHeader.getKey();
        var value = httpHeader.getValue();
        if(httpHeaderList.stream().noneMatch(header -> header.getKey().equals(name) && header.getValue().equals(value))){
            httpHeaderList.add(httpHeader);
        }
    }
    
    private boolean containsHeaderEntry(Set<Entry<String, List<String>>> headerEntriesSet, String headerName){
        return headerEntriesSet.stream().anyMatch(headerEntry -> headerEntry.getKey().equals(headerName));
    }
    
    private Entry<String, List<String>> readOnlyHeader(Entry<String, List<String>> headerEntry){
        return Map.entry(headerEntry.getKey(), headerEntry.getValue().stream().toList());
    }

    @Override
    public String toString() {
        return httpHeaderList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
    }
}
